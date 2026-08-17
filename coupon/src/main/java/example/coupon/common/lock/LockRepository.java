package example.coupon.common.lock;

import example.coupon.common.exception.type.CouponLockAcquisitionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LockRepository {

  private static final String GET_LOCK_SQL = "SELECT GET_LOCK(?, ?)";
  private static final String RELEASE_LOCK_SQL = "SELECT RELEASE_LOCK(?)";

  private final DataSource lockDataSource;

  public LockRepository(@Qualifier("lockDataSource") DataSource lockDataSource) {
    this.lockDataSource = lockDataSource;
  }

  public <T> T executeWithLock(String key, int timeoutSec, Supplier<T> work) {
    try (Connection conn = lockDataSource.getConnection()) {
      acquireLock(conn, key, timeoutSec);
      try {
        return work.get();
      } finally {
        releaseLock(conn, key);
      }
    } catch (SQLException e) {
      log.error("Lock connection error: key={}", key, e);
      throw new CouponLockAcquisitionException();
    }
  }

  private void acquireLock(Connection conn, String key, int timeoutSec) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(GET_LOCK_SQL)) {
      ps.setString(1, key);
      ps.setInt(2, timeoutSec);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          log.warn("GET_LOCK returned no row: key={}", key);
          throw new CouponLockAcquisitionException();
        }
        int result = rs.getInt(1);
        boolean wasNull = rs.wasNull();
        if (wasNull || result != 1) {
          log.warn("Lock acquisition failed: key={}, result={}", key, wasNull ? "NULL" : result);
          throw new CouponLockAcquisitionException();
        }
      }
    }
  }

  private void releaseLock(Connection conn, String key) {
    try (PreparedStatement ps = conn.prepareStatement(RELEASE_LOCK_SQL)) {
      ps.setString(1, key);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int result = rs.getInt(1);
          boolean wasNull = rs.wasNull();
          if (wasNull || result != 1) {
            log.warn("Lock release abnormal: key={}, result={}", key, wasNull ? "NULL" : result);
          }
        }
      }
    } catch (SQLException e) {
      log.error("Lock release SQL error: key={}", key, e);
    }
  }
}

package spring.tddhard.commerce;

public record CreateSellerCommand(
    String email,
    String username,
    String password
) {

}

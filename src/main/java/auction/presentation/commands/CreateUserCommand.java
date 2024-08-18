package auction.presentation.commands;

import auction.business.services.UserService;
import auction.data.models.User;
import auction.presentation.interfaces.ICommand;
import java.util.Scanner;

public class CreateUserCommand implements ICommand {

    private final UserService userService;
    private User userCreated;

    public CreateUserCommand(UserService userService) {
        this.userService = userService;
    }
    @Override
    public void execute() {
        createUser();
    }

    private void createUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome:");
        String name = scanner.nextLine();

        System.out.println("Digite o email:");
        String email = scanner.nextLine();

        System.out.println("Digite a senha:");
        String password = scanner.nextLine();

        this.userCreated = this.userService.create(name, email, password);

        System.out.println("Usuário criado com sucesso!");
    }

    public User getUserCreated() {
        return userCreated;
    }


}

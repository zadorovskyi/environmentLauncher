package environmentLauncer.environmentLauncer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import services.BundleManager;
import services.MdataManager;
import services.Users;
import services.Wso2UsersManager;

import java.util.Arrays;
import java.util.List;

@ShellComponent
public class StartEnvironment {
    private Wso2UsersManager manager;
    private MdataManager mdataManager;
    private BundleManager bundleManager;

    @Autowired
    public StartEnvironment(BundleManager bundleManager, MdataManager mdataManager, Wso2UsersManager manager) {
        this.bundleManager = bundleManager;
        this.manager = manager;
        this.mdataManager = mdataManager;
    }

    @ShellMethod("Method for start uploading.")
    public String start() {
        List<Users> users = Arrays.asList(Users.USER, Users.ADMINISTRATOR, Users.STEWARD, Users.SUPERVISOR,
                                          Users.USER_NO_DOMAIN);
        users.forEach(user -> {
            manager.createUser(user.getUserName(), user.getPassword(), user.getRoles());
        });

        String token = mdataManager.getToken();
        mdataManager.uploadMdata(token);
        bundleManager.uploadBundle();
        bundleManager.startServices();
        return "Operation seccussfully finished";
    }
}

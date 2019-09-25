package services

enum Users {
    USER('user', 'user123', []),
    ADMINISTRATOR('sysadmin', 'password1',
                  ['Internal/domain.CustomerMaster', 'Internal/domain.Customer', 'Internal/System_Administrator']),
    STEWARD('steward', 'password1',
            ['Internal/domain.CustomerMaster', 'Internal/Data_Steward', 'Internal/domain.Customer',
             'Internal/domain.CustomerPhoneMaster', 'Internal/domain.CustomerAddressMaster', 'Internal/domain.CustomerEmailMaster',
             'Internal/domain.CustomerPhone', 'Internal/domain.CustomerAddress']),
    SUPERVISOR('supervisor', 'password1',
               ['Internal/domain.CustomerMaster', 'Internal/Data_Supervisor', 'Internal/domain.Customer', '']),
    USER_NO_DOMAIN('nodomainuser', 'password1', [])

    String userName
    String password
    List roles

    Users(String userName, String password, List roles) {
        this.userName = userName
        this.password = password
        this.roles = roles
    }
}



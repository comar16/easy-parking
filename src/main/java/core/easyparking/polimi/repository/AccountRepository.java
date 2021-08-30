package core.easyparking.polimi.repository;


import core.easyparking.polimi.entity.Account;
import core.easyparking.polimi.utils.object.staticvalues.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByUsername(String username);

	Optional<Account> findByRoleAndUsername(Role role, String username);

	Optional<Account> findByRoleAndUsernameAndPassword(Role role, String username, String password);

	Optional<Account> findByRoleAndUsernameAndResetCode(Role role, String username, String resetCode);

	Optional<Account> deleteAccountByUsername(String username);

}
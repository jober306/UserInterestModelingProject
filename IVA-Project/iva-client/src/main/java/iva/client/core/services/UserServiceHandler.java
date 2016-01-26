package iva.client.core.services;

import iva.client.core.model.User;
import iva.client.exceptions.AuthenticationException;
import iva.client.security.PasswordHash;
import iva.client.web.repositories.UserRestRepository;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.stereotype.Service;

@Service
public class UserServiceHandler implements UserService {

	private final UserRestRepository userRepo;

	public UserServiceHandler() {
		this(new UserRestRepository());
	}

	public UserServiceHandler(UserRestRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public User create(String username, char[] password, String firstName, String lastName) {
		User user = new User(username, password, firstName, lastName);
		return userRepo.save(user);
	}

	@Override
	public User authenticate(String username, char[] password) throws AuthenticationException {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new AuthenticationException());
		try {
			if(PasswordHash.validatePassword(password, user.getPasswordHash())) {
				return user;
			} else {
				throw new AuthenticationException();
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public User updatePassword(String username, char[] oldPassword, char[] newPassword) throws AuthenticationException {
		User user = authenticate(username, oldPassword);
		user.setPassword(newPassword);
		return userRepo.save(user);
	}

	@Override
	public User update(User user) {
		if(!user.isNew()) {
			return userRepo.save(user);
		} else {
			return user;
		}
	}

	@Override
	public void delete(User user) {
		userRepo.delete(user);
	}

}

package com.scorpios.faceai.service.impl;

import com.scorpios.faceai.entity.Users;
import com.scorpios.faceai.repository.UserRepository;
import com.scorpios.faceai.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	@Override
	public void addUsers(Users users) {
		Users u = userRepository.save(users);
		System.out.println(u.getUserName());
	}
	@Override
	@Transactional
	public void updatePassword(String userName, String newPassword) {
		userRepository.updatePassword(userName, newPassword);
	}
	@Override
	@Transactional
	public void updateFace(String userName, String photoPath, String photoId){
		userRepository.updateFace(userName, photoPath, photoId);
	}
	@Override
	public Users selectUserByName(Users users) {
		Users u = null;
		if(users != null && !StringUtils.isEmpty(users.getUserName())) {
			u = userRepository.findByUserName(users.getUserName());
		}
		return u;
	}
	@Override
	public Users selectUserByName(String userName) {
		Users u = null;
		if(!StringUtils.isEmpty(userName)) {
			u = userRepository.findByUserName(userName);
		}
		return u;
	}
}

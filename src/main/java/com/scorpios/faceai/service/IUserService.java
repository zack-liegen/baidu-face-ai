package com.scorpios.faceai.service;


import com.scorpios.faceai.entity.Users;

public interface IUserService {

	public void addUsers(Users users);

	public void updatePassword(String userName, String newPassword);

	public Users selectUserByName(Users users);

	public Users selectUserByName(String userName);

	public void updateFace(String userName, String photoPath, String photoId);
}

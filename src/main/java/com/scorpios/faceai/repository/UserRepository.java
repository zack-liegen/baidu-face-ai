package com.scorpios.faceai.repository;


import com.scorpios.faceai.entity.Users;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	public Users findByUserName(String userName);
	@Modifying
	@Query(value = "UPDATE users SET user_password = :newPassword WHERE user_name = :userName", nativeQuery = true)
	public void updatePassword(@Param("userName") String userName, @Param("newPassword") String newPassword);

	@Modifying
	@Query(value = "UPDATE users SET user_photo_path = :photoPath, user_photo_id = :photoId WHERE user_name = :userName", nativeQuery = true)
	public void updateFace(@Param("userName") String userName, @Param("photoPath") String photoPath, @Param("photoId") String photoId);
}

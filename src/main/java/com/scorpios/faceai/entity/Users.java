package com.scorpios.faceai.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Entity
@Table(name="Users")
@Data
public class Users implements Serializable {


	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long userId;

    @Column(name="user_name")
	private String userName;

    @Column(name="user_password")
	private String userPassWord;

    @Column(name="user_photo_path",length = 2000)
	private String userPhotoPath;
	@Column(name="user_photo_id",length = 36)
	private String userPhotoId;
	@Column(name="user_realname")
	private String userRealname;
	@Column(name="user_telephone")
	private String userTelephone;
}

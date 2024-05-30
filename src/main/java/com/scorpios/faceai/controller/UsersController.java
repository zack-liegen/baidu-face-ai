package com.scorpios.faceai.controller;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.MatchRequest;
import com.scorpios.faceai.entity.Result;
import com.scorpios.faceai.entity.Users;
import com.scorpios.faceai.service.IUserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.scorpios.faceai.entity.MD5Utils.getMd5;

@RestController
@RequestMapping("/api")
public class UsersController {

	@Value("${file.path}")
	private String filePath;

	@Autowired
	private AipFace aipFace;

	@Autowired
	private IUserService userService;


	/**
	 * 注册
	 * @param userName
	 * @param password
	 * @param faceBase
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "register",method = RequestMethod.POST)
	public Result<String> register(String userName, String password, String faceBase) throws IOException {
		if(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(faceBase)) {
	        // 图片名称
	        String fileName = userName + System.currentTimeMillis() + ".png";
	        File file = new File(filePath + "/" + fileName);
			// 保存上传摄像头捕获的图片
			saveLocalImage(faceBase, file);
			// 向百度云人脸库插入
			String photoid =  faceSetAddUser(aipFace,faceBase,userName);
			// 往数据库里插入用户数据
			Users users = new Users();
			users.setUserName(userName);
			users.setUserPassWord(getMd5(userName,password));
			users.setUserPhotoPath(filePath + "/" + fileName);
			users.setUserPhotoId(photoid);
			userService.addUsers(users);
		}
		return Result.ok("注册成功");
	}
	/**
	 * 登录
	 * @param userName
	 * @param password
	 * @param faceBase
	 * @return
	 */
	@RequestMapping(value = "login",method = RequestMethod.POST)
	public Result<?> login(String userName, String password, String faceBase) {
		// 先确定密码是否正确
		Users users = userService.selectUserByName(userName);
		password = getMd5(userName,password);
		if( !users.getUserPassWord().equals(password) ) {
			return Result.error("密码错误！");
		}
		// 获取该用户保存的照片
		String fileName = users.getUserPhotoPath();
		String base64Image = getImageAsBase64(fileName);
		// String baseFaceBase = getImageStr(fileName);
		// 对比人脸
		if(verifyUser(base64Image, faceBase,aipFace) > 80) {
			return Result.ok("登录成功！");
		}
		if(verifyUser(base64Image, faceBase,aipFace) < 0.01) {
			return Result.ok("照片中未识别到人脸，请重新登录！");
		}
		return Result.error("核对面部信息失败，请重新登录！");
	}

	/**
	 * 获取人员信息
	 * @param userName
	 * @return
	 */
	@RequestMapping(value = "getUser",method = RequestMethod.GET)
	public Result<?> getUser(String userName) {
		Users users = userService.selectUserByName(userName);
		if(users == null){
			return Result.error("用户不存在！");
		}
		users.setUserPassWord(null);
		String fileName = users.getUserPhotoPath();
		String base64Image = getImageAsBase64(fileName);
		Map<String,Object> map = new HashMap<>();
		// 返回用户名和人像信息
		map.put("userName",users.getUserName());
		map.put("faceBase",base64Image);
		return Result.ok(map);
	}

	/**
	 * 修改密码
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @param faceBase
	 * @return
	 */
	@RequestMapping(value = "changePassword",method = RequestMethod.POST)
	public Result<?> changePassword(String userName, String oldPassword, String newPassword, String faceBase) {
		// 先确定密码是否正确
		Users users = userService.selectUserByName(userName);
		oldPassword = getMd5(userName,oldPassword);
		if( !users.getUserPassWord().equals(oldPassword) ) {
			return Result.error("密码错误！");
		}
		// 获取该用户保存的照片
		String fileName = users.getUserPhotoPath();
		String base64Image = getImageAsBase64(fileName);
		// 对比人脸
		if(verifyUser(base64Image, faceBase,aipFace) > 80) {
			users.setUserPassWord(getMd5(userName,newPassword));
			userService.updatePassword(users.getUserName(), users.getUserPassWord());
			System.out.println("修改成功"+users);
			return Result.ok("修改成功！");
		}
		if(verifyUser(base64Image, faceBase,aipFace) < 0.01) {
			return Result.ok("照片中未识别到人脸，请操作！");
		}
		return Result.error("核对面部信息失败，请重新登录操作！");
	}

	/**
	 * 修改人像
	 * @param userName
	 * @param password
	 * @param faceBase
	 * @return
	 */
	@RequestMapping(value = "changeFace",method = RequestMethod.POST)
	public Result<?> changeFace(String userName, String password, String faceBase) {
		try{
			// 先确定密码是否正确
			Users users = userService.selectUserByName(userName);
			password = getMd5(userName,password);
			if( !users.getUserPassWord().equals(password) ) {
				return Result.error("密码错误！");
			}
			// 获取该用户保存的照片
			String fileName = users.getUserPhotoPath();
			String base64Image = getImageAsBase64(fileName);
			// 对比人脸
			if(verifyUser(base64Image, faceBase,aipFace) > 80) {
				fileName = userName + System.currentTimeMillis() + ".png";
				File file = new File(filePath + "/" + fileName);
				// 保存本地上传摄像头捕获的图片
				saveLocalImage(faceBase, file);
				users.setUserPhotoPath(filePath + "/" + fileName);
				// 向百度人脸库中更新
				String photoid =  faceSetUpdateUser(aipFace,faceBase,userName);
				users.setUserPhotoId(photoid);
				userService.updateFace(userName, users.getUserPhotoPath(), users.getUserPhotoId());
				return Result.ok("修改成功！");
			}
			if(verifyUser(base64Image, faceBase,aipFace) < 0.01) {
				return Result.ok("照片中未识别到人脸，请操作！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.toString());
		}
        return Result.error("请联系管理员！");
    }
	/**
	 * 判断该用户是否已被注册
	 * @param userName
	 * @return true 已注册，false 未注册
	 */
	@RequestMapping(value = "checkUserExist",method = RequestMethod.GET)
	public Result<Boolean> checkUserExist( String userName ) {
		Users users = new Users();
		users.setUserName(userName);
		if(userService.selectUserByName(users) != null) {
			return Result.ok(true);
		}
		return Result.ok(false);
	}


	/**
	 * 人脸比对
	 * @return 返回相似度（百分制）
	 */
	public Double verifyUser(String faceBase, String baseFaceBase,AipFace client) {
		MatchRequest req1 = new MatchRequest(faceBase, "BASE64");
		MatchRequest req2 = new MatchRequest(baseFaceBase, "BASE64");
		ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
		requests.add(req1);
		requests.add(req2);
		JSONObject res = client.match(requests);
		System.out.println(res.toString(2));
		try {
			JSONObject result = (JSONObject) res.getJSONObject("result");
			return (Double) result.get("score");
		}
		catch (Exception e) {
			// 照片中没有人脸
			return -1.0;
		}
	}


	/**
	 * 将Base64编码的图像数据保存到服务器本地文件。
	 * @param imgStr Base64编码的图像字符串。
	 * @param file 指定保存图像的文件路径。
	 * @return 如果图像成功保存，则返回true；如果图像数据为空或保存失败，则返回false。
	 */
	public boolean saveLocalImage(String imgStr, File file) {
		// 图像数据为空
		if (imgStr == null) {
			return false;
		}else {

			BASE64Decoder decoder = new BASE64Decoder();
			try {
				// Base64解码
				byte[] bytes = decoder.decodeBuffer(imgStr);
				for (int i = 0; i < bytes.length; ++i) {
					if (bytes[i] < 0) {
						bytes[i] += 256;
					}
				}
				// 生成jpeg图片
				if(!file.exists()) {
					file.getParentFile().mkdir();
					OutputStream out = new FileOutputStream(file);
					out.write(bytes);
					out.flush();
					out.close();
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	/**
	 * 将图片文件转换为Base64编码的字符串。
	 *
	 * @param imagePath 图片文件的路径。
	 * @return 图片文件的Base64编码字符串。如果文件不存在或读取发生异常，则返回null。
	 */
	public static String getImageAsBase64(String imagePath) {
		File imageFile = new File(imagePath);
		if (!imageFile.exists()) {
			System.err.println("File does not exist.");
			return null;
		}

		try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
			byte[] imageData = new byte[(int) imageFile.length()];
			fileInputStream.read(imageData);
			return Base64.getEncoder().encodeToString(imageData);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 向人脸数据库中添加用户。
	 *
	 * @param client AipFace客户端，用于与人脸识别服务进行交互。
	 * @param faceBase 人脸底库的ID，标识要操作的人脸底库。
	 * @param username 要添加的用户的名称。
	 * @return 返回添加用户后生成的人脸标识（face_token），便于后续的人脸识别操作。
	 */
	public String faceSetAddUser(AipFace client, String faceBase, String username) {
		// 参数为数据库中注册的人脸
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("user_info", "user's info");
		JSONObject res = client.addUser(faceBase, "BASE64", "user_01", username, options);
		JSONObject result = (JSONObject) res.getJSONObject("result");
		System.out.println(result.toString(2));
		// 返回人脸图片的唯一标识
        return result.optString("face_token");
	}

	/**
	 * 更新用户信息到人脸数据库
	 *
	 * @param client AipFace对象，用于与人脸识别服务端进行交互
	 * @param faceBase 人脸数据库的标识
	 * @param username 需要更新信息的用户名
	 * @return 更新成功后，返回用户的face_token
	 */
	public String faceSetUpdateUser(AipFace client, String faceBase, String username) {
		// 参数为数据库中注册的人脸
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("user_info", "user's info");
		JSONObject res = client.updateUser(faceBase, "BASE64", "user_01", username, options);
		JSONObject result = (JSONObject) res.getJSONObject("result");
		System.out.println(result.toString(2));
		// 返回人脸图片的唯一标识
		return result.optString("face_token");
	}
}

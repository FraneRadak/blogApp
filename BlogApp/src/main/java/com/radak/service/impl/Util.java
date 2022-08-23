package com.radak.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.radak.database.entities.Post;
import com.radak.utils.FileUploadUtil;

public class Util {
	public static void savePicture(Post post, MultipartFile multipartFile) {
		String uploadDir = "photos";
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String fullFilePath = "/photos/" + fileName;
		post.setPhotoPath(fullFilePath);
		try {
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void deletePicture(Post post) {
		String path = post.getPhotoPath();
		Path fileToDeletePath = Paths.get(path.substring(1));
		try {
			Files.delete(fileToDeletePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

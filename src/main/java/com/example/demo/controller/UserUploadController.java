package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Service.FileStorageService;
import com.example.demo.model.User;
import com.example.demo.properties.FileStorageProperties;

@RestController
public class UserUploadController {

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	FileStorageProperties fileStorageProperties;

	private Path fileStorageLocation;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ArrayList<User> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path targetLocation = this.fileStorageLocation.resolve(fileName);
		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		ArrayList<User> list = new ArrayList<User>();
		try (Stream<String> stream = Files.lines(targetLocation.toAbsolutePath())) {

			stream.forEach(line -> list.add(new User(line)));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

}

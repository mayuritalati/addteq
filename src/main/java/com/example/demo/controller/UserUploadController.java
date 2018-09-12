package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.User;
import com.example.demo.properties.FileStorageProperties;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping(value="/user")
public class UserUploadController {

	@Autowired
	private FileStorageProperties fileStorageProperties;

	
	private Path fileStorageLocation;
	
	@Autowired
	private UserRepository userRepository;

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
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String uploadAndSaveFile(@RequestParam("file") MultipartFile file) throws IOException {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path targetLocation = this.fileStorageLocation.resolve(fileName);
		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		ArrayList<User> list = new ArrayList<User>();
		try (Stream<String> stream = Files.lines(targetLocation.toAbsolutePath())) {

			stream.forEach(line -> userRepository.save(new User(line)));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "saved sucessfully";
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public User getUserByName(@RequestParam("name") String name) throws IOException {
		User user = (User) userRepository.findByName(name);
		return user;
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ArrayList<User> getUserByName() throws IOException {
		ArrayList<User> list  = (ArrayList<User>) userRepository.findAll();
		return list;
	}
	
	
	

}

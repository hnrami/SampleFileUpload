package net.snv.iam.amc.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.snv.iam.amc.util.UploadFileUtil;
import net.snv.iam.amc.util.UploadUtil;

@RestController
@RequestMapping("/amc/ctl/uploadapi")
public class UploadFileController {

	
		private static String UPLOADED_FOLDER = null;
	// Multiple file upload
		@RequestMapping(value = "/multi", method = RequestMethod.POST)
		@ResponseBody
	    public ResponseEntity<?> uploadFileMulti(
	            @RequestParam("to") String to,
	            @RequestParam("subject") String subject,
	            @RequestParam("body") String body,
	            @RequestParam("files") MultipartFile[] uploadfiles) throws Exception {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			System.out.println("USERNAME:-----"+auth.getName());
			
			System.out.println("THISIS :::---"+to);
			System.out.println("THISIS :::---"+subject);
			System.out.println("THISIS :::---"+body);
			UPLOADED_FOLDER = "D://temp//";
	        String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
	                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
//
//	        if (StringUtils.isEmpty(uploadedFileName)) {
//	            return new ResponseEntity("please select a file!", HttpStatus.OK);
//	        }

	        try {
	        	String username=auth.getName();
	        	if(!( username.equalsIgnoreCase("manager") || username.equalsIgnoreCase("xc311") || username.equalsIgnoreCase("82924"))){
	        		 throw new Exception("NOT VALID USER To PERFORM this ACTION");
	        	}else{
	        		
	        		System.out.println("uploadfiles"+uploadfiles.length);
	        		String resultPath = saveUploadedFiles(Arrays.asList(uploadfiles));
		        	System.out.println("resultPath"+resultPath);
		        	if((!to.isEmpty()) && to!=null){
		        		UploadFileUtil.entryPoint(resultPath,to,body,subject);
		        	}
		        	else{
		        		UploadFileUtil.entryPoint(resultPath,"defaultmanger@id.com",body,subject);
		        	}
		            System.out.println("End");
	        	}
	        	
	        } catch (IOException e) {
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }

	        return new ResponseEntity("Successfully uploaded - "
	                + uploadedFileName, HttpStatus.OK);

	    }
		
		
		 private String saveUploadedFiles(List<MultipartFile> files) throws IOException {

			 	String pathCurrentTime =Long.toString(new Date().getTime());
			 	 new File(UPLOADED_FOLDER+pathCurrentTime).mkdir();
			 	UPLOADED_FOLDER=UPLOADED_FOLDER+pathCurrentTime+"//";
			 	 System.out.println("pathCurrentTime"+pathCurrentTime);
			 	 for (MultipartFile file : files){
			 		 
			 		if (file.isEmpty()) {
			 			continue;
		            }
		            byte[] bytes = file.getBytes();
		            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
		            System.out.println("path"+path);
		            Files.write(path, bytes);

			 	 }
		       
		        return UPLOADED_FOLDER;
		    }
}

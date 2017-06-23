package net.snv.iam.amc.util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
public class UploadFileUtil {
	
	static String globalPath=null;

	static String resultFileName="mergeFile.pdf";
	@SuppressWarnings("deprecation")
	public static void entryPoint(String path,String emailID,String body,String subject) throws IOException {
		
			globalPath	= path;
			
			
			try(Stream<Path> paths = Files.walk(Paths.get(globalPath))) 
			{
			    paths.forEach(filePath -> {
			        if (Files.isRegularFile(filePath)) {
			           if(FilenameUtils.getExtension(filePath.getFileName().toString()).equalsIgnoreCase("doc")){
			            	Converter converter;
			            	InputStream inStream;
							try {
								System.out.println("DOC File Paring");
								inStream = getInFileStream(filePath.toString());
								OutputStream outStream = getOutFileStream(filePath.getParent()+"\\"+FilenameUtils.removeExtension(filePath.getFileName().toString())+".pdf");
				        		converter = new DocToPDFConverter(inStream, outStream, true, true);
				        		converter.convert();
				        		System.out.println("DOC File Paring Done");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			        	}
			            else if(FilenameUtils.getExtension(filePath.getFileName().toString()).equalsIgnoreCase("docx")){
			            	Converter converter;
			            	InputStream inStream;
							try {
								System.out.println("DOCX File Paring");
								inStream = getInFileStream(filePath.toString());
								OutputStream outStream = getOutFileStream(filePath.getParent()+"\\"+FilenameUtils.removeExtension(filePath.getFileName().toString())+".pdf");
				        		converter = new DocxToPDFConverter(inStream, outStream, true, true);
				        		converter.convert();
				        		System.out.println("DOCX File Paring Done");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			        	}
			            else if(FilenameUtils.getExtension(filePath.getFileName().toString()).equalsIgnoreCase("ppt")){
			            	Converter converter;
			            	InputStream inStream;
							try {
								System.out.println("PPT File Paring");
								inStream = getInFileStream(filePath.toString());
								OutputStream outStream = getOutFileStream(filePath.getParent()+"\\"+FilenameUtils.removeExtension(filePath.getFileName().toString())+".pdf");
				        		converter = new PptToPDFConverter(inStream, outStream, true, true);
				        		converter.convert();
				        		System.out.println("PPT File Paring Done");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			        	}
			            else if(FilenameUtils.getExtension(filePath.getFileName().toString()).equalsIgnoreCase("pptx")){
			            	Converter converter;
			            	InputStream inStream;
							try {
								System.out.println("PPTX File Paring ");
								inStream = getInFileStream(filePath.toString());
								OutputStream outStream = getOutFileStream(filePath.getParent()+"\\"+FilenameUtils.removeExtension(filePath.getFileName().toString())+".pdf");
				        		converter = new PptxToPDFConverter(inStream, outStream, true, true);
				        		converter.convert();
				        		System.out.println("PPTX File Paring Done ");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			        	}
			            else if(FilenameUtils.getExtension(filePath.getFileName().toString()).equalsIgnoreCase("txt")){
			            	
			            	File file=new File(filePath.toString());
			            	convertTextfileToPDF(file);
			            }
			           
			        }
			    });
			    
			    
			} 
			
			
			System.out.println("PDF File mergerstart ");
			PDFMergerUtility PDFmerger = new PDFMergerUtility();
			 PDFmerger.setDestinationFileName(globalPath+resultFileName);
			 
			 try(Stream<Path> paths = Files.walk(Paths.get(globalPath))) {
				    paths.forEach(filePath -> {
				    	if (Files.isRegularFile(filePath)) {
				    		
				    		if(FilenameUtils.getExtension(filePath.getFileName().toString()).equalsIgnoreCase("pdf")){
				    			
				    			 try {
									PDFmerger.addSource(filePath.getParent()+"\\"+filePath.getFileName());
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				    		}
				    	}
				    });
				    
			 }
			 
			 PDFmerger.mergeDocuments();
			 
			 System.out.println("PDF File merger Done ");
			 try(Stream<Path> paths = Files.walk(Paths.get(globalPath))) {
				    paths.forEach(filePath -> {
				    	if (Files.isRegularFile(filePath)) {
				    				if(!filePath.getFileName().toString().equalsIgnoreCase(resultFileName)){
				    					File file = new File(filePath.getParent()+"\\"+filePath.getFileName());
					    				file.delete();
				    				}
				    		}
				    });
			 }
			 System.out.println("PDF Extra File Deleted ");	    	

			 System.out.println("Now Start Email Sending");
			 
			 EmailSend(globalPath+resultFileName,emailID,body,subject);
			 System.out.println("Email Sent Successfullly ");
			 
			 
			
}

	public static void EmailSend(String file,String emailID,String body,String subject){
//		String to = "ramihemang@gmail.com";
		String to =emailID;
	      // Sender's email ID needs to be mentioned
	      String from = "springboot@gmail.com";

	      final String username = "springboot2017@gmail.com";//change accordingly
	      final String password = "change";//change accordingly

	      // Assuming you are sending email through relay.jangosmtp.net
	      String host = "smtp.gmail.com";

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");

	      
	      

	      
			
	      // Get the Session object.
	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(username, password);
	            }
	         });

	      try {
	         // Create a default MimeMessage object.
	         Message message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.setRecipients(Message.RecipientType.TO,
	            InternetAddress.parse(to));

	         // Set Subject: header field
	         if((!subject.isEmpty()) && subject!=null)
	        	 message.setSubject(subject);
	         else
	        	 message.setSubject("Testing Subject");
	         // Create the message part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
	         if((!body.isEmpty()) && body!=null)
	        	 messageBodyPart.setText(body);
	         else	 
	        	 messageBodyPart.setText("This is message body");

	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         messageBodyPart = new MimeBodyPart();
	         String filename = file;
	         DataSource source = new FileDataSource(filename);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart);

	         // Send message
	         Transport.send(message);

	         System.out.println("Sent message successfully....");
	  
	      } catch (MessagingException e) {
	         throw new RuntimeException(e);
	      }
	}
	
	protected static InputStream getInFileStream(String inputFilePath) throws FileNotFoundException{
		File inFile = new File(inputFilePath);
		FileInputStream iStream = new FileInputStream(inFile);
		return iStream;
	}
	
	protected static OutputStream getOutFileStream(String outputFilePath) throws IOException{
		File outFile = new File(outputFilePath);
		
		try{
			//Make all directories up to specified
			outFile.getParentFile().mkdirs();
		} catch (NullPointerException e){
			//Ignore error since it means not parent directories
		}
		
		outFile.createNewFile();
		FileOutputStream oStream = new FileOutputStream(outFile);
		return oStream;
	}
	public static boolean convertTextfileToPDF(File file)
	 {
	  FileInputStream iStream=null;
	  DataInputStream in=null;
	  InputStreamReader is=null;
	  BufferedReader br=null;
	  try {
	      Document pdfDoc = new Document();
	       
	    String text_file_name =file.getParent()+"\\"+new Date().getTime()+".pdf";
	    PdfWriter.getInstance(pdfDoc,new FileOutputStream(text_file_name));
	    pdfDoc.open();
	    pdfDoc.setMarginMirroring(true);
	    pdfDoc.setMargins(36, 72, 108,180);
	    pdfDoc.topMargin();
	    Font normal_font = new Font();
	    Font bold_font = new Font();
	    bold_font.setStyle(Font.BOLD);
	    bold_font.setSize(10);
	    normal_font.setStyle(Font.NORMAL);
	    normal_font.setSize(10);
	    pdfDoc.add(new Paragraph("\n"));
	    if(file.exists())
	    {
	    iStream = new FileInputStream(file);
	     in = new DataInputStream(iStream);
	     is=new InputStreamReader(in);
	     br = new BufferedReader(is);
	    String strLine;
	    while ((strLine = br.readLine()) != null)   {
	     Paragraph para =new Paragraph(strLine+"\n",normal_font);
	     para.setAlignment(Element.ALIGN_JUSTIFIED);
	     pdfDoc.add(para);
	    }
	    }   
	    else
	    {
	     System.out.println("file does not exist");
	     return false;
	    }
	    pdfDoc.close(); 
	  }
	   
	  catch(Exception e)
	  {
	   System.out.println("FileUtility.covertEmailToPDF(): exception = " + e.getMessage());
	  }
	  finally
	  {
	    
	    try {
	     if(br!=null)
	     {
	     br.close();
	     }
	     if(is!=null)
	     {
	     is.close();
	     }
	     if(in!=null)
	     {
	     in.close();
	     }
	     if(iStream!=null)
	     {
	      iStream.close();
	     }
	    } catch (IOException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	    }
	    
	  }
	  return true;
	 }


}

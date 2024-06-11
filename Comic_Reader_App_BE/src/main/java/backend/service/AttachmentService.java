package backend.service;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend.constant.Constant;
import backend.model.Attachment;
import backend.model.Chapter;
import backend.model.Comic;
import backend.repository.AttachmentRepostory;
import backend.repository.ChapterRepository;
import jakarta.transaction.Transactional;

@Service
public class AttachmentService {
	@Autowired
	AttachmentRepostory attachmentRepo;

	@Autowired
	ChapterRepository chapterRepo;

	@Transactional
	public List<Attachment> getAttachmentList(Long chapterId, Comic comic) {
		Chapter chapter = chapterRepo.getChapterByIdAndComic(chapterId, comic);
		List<Attachment> attachementList = attachmentRepo.getByChapter(chapter);
		return attachementList;
	}

	public static Dimension getScaledDimension(Dimension imageSize, Dimension boundary) {
		int original_width = imageSize.width;
		int original_height = imageSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;
		int new_width = original_width;
		int new_height = original_height;

		if (original_width > bound_width) {
			new_width = bound_width;
			new_height = (new_width * original_height) / original_width;
		}

		if (new_height > bound_height) {
			new_height = bound_height;
			new_width = (new_height * original_width) / original_height;
		}

		return new Dimension(new_width, new_height);
	}

	public byte[] resizeImage(BufferedImage image) {
		Dimension newMaxSize = getScaledDimension(new Dimension(image.getWidth(), image.getHeight()),
				new Dimension(800, 1440));
		BufferedImage resizedImg = Scalr.resize(image, Method.AUTOMATIC, newMaxSize.width, newMaxSize.height);

		BufferedImage newBufferedImage = new BufferedImage(resizedImg.getWidth(), resizedImg.getHeight(),
				BufferedImage.TYPE_INT_RGB);

//		newBufferedImage.getGraphics().drawImage(resizedImg, 0, 0, null);
		Graphics2D g = newBufferedImage.createGraphics();
		g.drawImage(resizedImg, 0, 0, null);
		g.dispose();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(newBufferedImage, "jpg", outputStream);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		byte[] imageBytes = outputStream.toByteArray();

		return imageBytes;
	}

	public void addAttachment(MultipartFile attach, Chapter chapter) {
		UUID uuid = UUID.randomUUID();
		try {
			byte[] image = resizeImage(ImageIO.read(attach.getInputStream()));
			String imagePath = Constant.ImagePath + "/" + uuid + ".jpg";
			OutputStream os = Files.newOutputStream(Paths.get(Constant.ImagePath + "/" + uuid + ".jpg"));
			os.write(image);
			os.close();

			Attachment attachment = Attachment.builder().chapter(chapter).attachmentPath(imagePath).build();
			attachmentRepo.save(attachment);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void addAttachment(File file, Chapter chapter) {
		UUID uuid = UUID.randomUUID();
		try {
			byte[] imageByte = resizeImage(ImageIO.read(file));
			String imagePath = Constant.ImagePath + "/" + uuid + ".jpg";
			OutputStream os = Files.newOutputStream(Paths.get(Constant.ImagePath + "/" + uuid + ".jpg"));
			os.write(imageByte);
			os.close();

			Attachment attachment = Attachment.builder().chapter(chapter).attachmentPath(imagePath).build();
			attachmentRepo.save(attachment);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void updateAttachment(Long id, MultipartFile imageFile, Chapter chapter, String imageOrder) {
		Attachment updateAttach = attachmentRepo.getAttachmentById(id);
		if (imageOrder == null) {
			imageOrder = updateAttach.getImageOrder();
		}
		UUID newUUID = UUID.randomUUID();
		deleteAttachment(id);
		try {
			String newAttachment = Constant.ImagePath + "/" + newUUID + ".jpg";
			OutputStream os = Files.newOutputStream(Paths.get(Constant.ImagePath + "/" + newUUID + ".jpg"));
			os.write(resizeImage(ImageIO.read(imageFile.getInputStream())));
			os.close();
			attachmentRepo.save(
					Attachment.builder().chapter(chapter).imageOrder(imageOrder).attachmentPath(newAttachment).build());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteAttachment(Long id) {
		Attachment attachment = attachmentRepo.getAttachmentById(id);
		String fileImage = attachment.getAttachmentPath();
		File imageFile = new File(fileImage);
		try {
			Files.delete(imageFile.toPath());
			attachmentRepo.deleteById(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public List<File> getUnzippedFiles(MultipartFile zippedFile) {
		List<File> unzippedFiles = new ArrayList<>();
		try {
			ZipInputStream zipInputStream = new ZipInputStream(zippedFile.getInputStream());
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (!zipEntry.isDirectory()) {
					File tempFile = File.createTempFile("unzipped", ".jpg");
					try (FileOutputStream fos = new FileOutputStream(tempFile)) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = zipInputStream.read(buffer)) != -1) {
							fos.write(buffer, 0, bytesRead);
						}
					}
					unzippedFiles.add(tempFile);
				}
				zipInputStream.closeEntry();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return unzippedFiles;
	}

	public void uploadAttachmentZip(MultipartFile multipartFile, Chapter chapter) {
		List<File> attachmentList = getUnzippedFiles(multipartFile);
		for (File file : attachmentList) {
			addAttachment(file, chapter);
		}
	}

}

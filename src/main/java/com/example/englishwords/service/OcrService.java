package com.example.englishwords.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class OcrService {
    
    private final ITesseract tesseract;
    
    public OcrService() {
        tesseract = new Tesseract();
        // 设置tessdata路径
        try {
            // 设置tessdata目录路径
            tesseract.setDatapath("./tessdata");
            System.out.println("OCR Service initialized with tessdata path: ./tessdata");
        } catch (Exception e) {
            System.err.println("Failed to initialize OCR service: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从图片文件中提取文本
     * @param imageFile 图片文件
     * @return 提取的文本
     * @throws IOException IO异常
     * @throws TesseractException Tesseract异常
     */
    public String extractTextFromImage(MultipartFile imageFile) throws IOException, TesseractException {
        BufferedImage bufferedImage = null;
        try {
            System.out.println("Processing image file: " + imageFile.getOriginalFilename());
            System.out.println("File size: " + imageFile.getSize());
            
            // 读取图片到内存
            byte[] imageBytes = imageFile.getBytes();
            bufferedImage = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
            if (bufferedImage == null) {
                throw new IOException("Failed to read image file");
            }
            
            String result = tesseract.doOCR(bufferedImage);
            System.out.println("OCR result: " + result);
            return result;
        } catch (IOException e) {
            System.err.println("IO error during OCR processing: " + e.getMessage());
            throw e;
        } catch (TesseractException e) {
            System.err.println("Tesseract error during OCR processing: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during OCR processing: " + e.getMessage());
            throw new TesseractException("Unexpected error: " + e.getMessage(), e);
        } finally {
            // 释放图片资源
            if (bufferedImage != null) {
                bufferedImage.flush();
            }
        }
    }
    
    /**
     * 从图片文件中提取文本（带语言设置）
     * @param imageFile 图片文件
     * @param language 语言代码（如 "eng" 英语, "chi_sim" 简体中文）
     * @return 提取的文本
     * @throws IOException IO异常
     * @throws TesseractException Tesseract异常
     */
    public String extractTextFromImage(MultipartFile imageFile, String language) throws IOException, TesseractException {
        try {
            System.out.println("Processing image file with language '" + language + "': " + imageFile.getOriginalFilename());
            tesseract.setLanguage(language);
            return extractTextFromImage(imageFile);
        } catch (Exception e) {
            System.err.println("Error during OCR processing with language '" + language + "': " + e.getMessage());
            throw e;
        }
    }
}
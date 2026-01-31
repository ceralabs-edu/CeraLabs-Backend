package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ExecutorService executor =
            Executors.newFixedThreadPool(6);

    @Override
    public BufferedImage concatQuestionImages(AssignmentQuestion question) {
        List<String> imageUrls = buildOrderedUrls(question);

        List<BufferedImage> images = downloadImagesParallel(imageUrls);

        if (images.isEmpty()) {
            throw new IllegalStateException("No images to concat");
        }

        return concatVertical(images);
    }

    private List<String> buildOrderedUrls(AssignmentQuestion question) {
        List<String> urls = new ArrayList<>();

        urls.add(question.getQuestionImageUrl());

        if (question.getAnswerImageUrls() != null
                && !question.getAnswerImageUrls().isEmpty()) {
            urls.addAll(question.getAnswerImageUrls());
        }

        if (question.getExplainImageUrl() != null) {
            urls.add(question.getExplainImageUrl());
        }

        return urls;
    }

    private List<BufferedImage> downloadImagesParallel(List<String> urls) {
        try {
            List<Callable<BufferedImage>> tasks = urls.stream()
                    .map(url -> (Callable<BufferedImage>) () -> downloadImage(url))
                    .toList();

            List<Future<BufferedImage>> futures = executor.invokeAll(tasks);

            List<BufferedImage> images = new ArrayList<>();
            for (Future<BufferedImage> f : futures) {
                BufferedImage img = f.get();
                if (img != null) {
                    images.add(img);
                }
            }
            return images;

        } catch (Exception e) {
            throw new RuntimeException("Failed to download images", e);
        }
    }

    private BufferedImage downloadImage(String url) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            return ImageIO.read(in);
        }
    }

    private BufferedImage concatVertical(List<BufferedImage> images) {
        int width = images.stream()
                .mapToInt(BufferedImage::getWidth)
                .max()
                .orElse(0);

        int height = images.stream()
                .mapToInt(BufferedImage::getHeight)
                .sum();

        BufferedImage combined =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = combined.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);

        int currentY = 0;
        for (BufferedImage img : images) {
            g.drawImage(img, 0, currentY, null);
            currentY += img.getHeight();
        }

        g.dispose();
        return combined;
    }
}

package app.demo.neurade.services;

import app.demo.neurade.domain.models.assignment.AssignmentQuestion;

import java.awt.image.BufferedImage;

public interface ImageService {
    BufferedImage concatQuestionImages(AssignmentQuestion question);
}

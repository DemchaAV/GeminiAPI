import org.gemini.core.client.GeminiConnection;
import org.gemini.core.client.model.ImagenModel;
import org.gemini.core.client.model.enums.VerAPI;
import org.gemini.core.client.model.enums.imagen.ImagenGenerateMethod;
import org.gemini.core.client.model.enums.imagen.ImagenVariation;
import org.gemini.core.client.model.enums.imagen.ImagenVersion;
import org.gemini.core.client.request_response.content.Image;
import org.gemini.core.client.request_response.request.ImgGenRequest;
import org.gemini.core.client.request_response.Instance;
import org.gemini.core.client.request_response.parameters_image_request.Parameters;
import org.gemini.core.client.request_response.parameters_image_request.enums_image_gen.AspectRatio;
import org.gemini.core.client.request_response.parameters_image_request.enums_image_gen.PersonGeneration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ImageGeneration {
    public static void main(String[] args) {

        String PROMPT = TestImgResponse.readTextFileFromResources("ImageGenPrompt.txt");
//        String PROMPT = "Passion fruit martini, image should be with clean white background";


        GeminiConnection connection = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .imagenModel(ImagenModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .generateMethod(ImagenGenerateMethod.PREDICT)
                        .variation(ImagenVariation._3_0)
                        .version(ImagenVersion.GENERATE_001)
                        .build())
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .build();

        ImgGenRequest imageRequest = ImgGenRequest.builder()
                .instances(List.of(Instance.builder()
                                .prompt(PROMPT)
                                .build()
                        )
                )
                .parameters(Parameters.builder()
                        .sampleCount(1)
                        .personGeneration(PersonGeneration.allow_adult)
                        .aspectRatio(AspectRatio.RATIO_9_16)
                        .build())
                .build();
        var responseOptional = connection.sendRequest(imageRequest).getResponse(true);


        String pathFolder = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\";
        String fileName = "pinkFlowers";

        responseOptional.ifPresent(response -> {
            List<Image> images = Image.extractPack(response, "jpeg");
            Image.writeTo(images, pathFolder, fileName);
        });
    }


    class ImageFrame extends JFrame {
        public ImageFrame(String imagePath) {
            ImagePanel panel = new ImagePanel(imagePath);
            add(panel);
            pack(); // Adjust frame size to panel's preferred size
            setTitle("Original Size Image Viewer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public ImageFrame(Path imagePath) {
            ImagePanel panel = new ImagePanel(imagePath);
            add(panel);
            pack(); // Adjust frame size to panel's preferred size
            setTitle("Original Size Image Viewer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        class ImagePanel extends JPanel {
            private BufferedImage image;

            public ImagePanel(String imagePath) {
                try {
                    image = ImageIO.read(new File(imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public ImagePanel(Path imagePath) {
                try {
                    image = ImageIO.read(imagePath.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, this); // Draw at original size
                }
            }

            @Override
            public Dimension getPreferredSize() {
                if (image != null) {
                    return new Dimension(image.getWidth(), image.getHeight());
                } else {
                    return super.getPreferredSize();
                }
            }
        }
    }

}


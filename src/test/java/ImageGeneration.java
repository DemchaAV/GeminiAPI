import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.ImagenModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.imagen.ImagenGenerateMethod;
import io.github.demchaav.gemini.model.enums.imagen.ImagenVariation;
import io.github.demchaav.gemini.model.enums.imagen.ImagenVersion;
import io.github.demchaav.gemini.request_response.Instance;
import io.github.demchaav.gemini.request_response.content.Image;
import io.github.demchaav.gemini.request_response.parameters_image_request.Parameters;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.AspectRatio;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.SafetySetting;
import io.github.demchaav.gemini.request_response.request.ImgGenRequest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImageGeneration {
    public static void main(String[] args) throws URISyntaxException, IOException {


//        String PROMPT = "Passion fruit martini, image should be with clean white background";

        String PROMPT = Files.readString( Path.of( Thread.currentThread().getContextClassLoader().getResource("prompt.txt").toURI()));

        GeminiConnection connection = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .imagenModel(ImagenModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .generateMethod(ImagenGenerateMethod.PREDICT)
                        .variation(ImagenVariation._3_0)
                        .version(ImagenVersion.GENERATE_002)
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
                        .aspectRatio(AspectRatio.RATIO_3_4)
                        .safetySetting(SafetySetting.block_low_and_above)
                        .build())
                .build();
        var responseOptional = connection.sendRequest(imageRequest).getImageResponse();


        String pathFolder = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\";
        String fileName = "cocktail";

        responseOptional.ifPresent(response -> {
            List<Image> images = Image.extractPack(response, "jpeg");
            Image.writeTo(images, pathFolder, fileName);
            System.out.println(pathFolder + "\\" + fileName + "." + images.getFirst().getFormat());
            ImageFrame imageFrame = new ImageFrame(pathFolder + "\\" + fileName + "(0)." + images.getFirst().getFormat());
        });
    }


    static class ImageFrame extends JFrame {

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
                    return new Dimension(image.getWidth()/2, image.getHeight()/2);
                } else {
                    return super.getPreferredSize();
                }
            }
        }
    }

}


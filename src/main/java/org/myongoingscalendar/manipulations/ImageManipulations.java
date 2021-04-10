package org.myongoingscalendar.manipulations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.myongoingscalendar.model.Image;
import org.myongoingscalendar.model.MIMEType;
import org.myongoingscalendar.utils.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author firs
 */
@Slf4j
@Service
public class ImageManipulations {

    @Value("${images.path.windows}")
    private String windowsImagesPath;
    @Value("${images.path.linux}")
    private String linuxImagesPath;
    @Value("${links.webp.address}")
    private String webpPath;
    @Value("${links.webp.port}")
    private String webpPort;

    public Boolean validateAvatar(MultipartFile avatar) {
        List<String> formats = Arrays.asList("image/jpg", "image/jpeg", "image/png");
        int width = 200;
        int height = 200;
        int size = 150000;

        if (formats.stream().noneMatch(e -> e.equals(avatar.getContentType())) || avatar.getSize() > size)
            return false;

        try (InputStream input = avatar.getInputStream()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(input);
                return bufferedImage.getWidth() <= width && bufferedImage.getHeight() <= height;
            } catch (Exception e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public String saveAvatar(MultipartFile avatar) {
        Path originalAvatarFolderPath = Paths.get(getAvatarImagesLocationPath(), "original");
        Path convertedAvatarFolderPath = Paths.get(getAvatarImagesLocationPath(), "webp");
        if (!new File(originalAvatarFolderPath.toUri()).exists()) new File(originalAvatarFolderPath.toUri()).mkdir();
        if (!new File(convertedAvatarFolderPath.toUri()).exists()) new File(convertedAvatarFolderPath.toUri()).mkdir();
        File file = null;
        Path to = null;
        try {
            String avatarName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(avatar.getOriginalFilename());
            file = new File(Paths.get(originalAvatarFolderPath.toString(), avatarName).toUri());
            to = Paths.get(convertedAvatarFolderPath.toString(), FilenameUtils.getBaseName(file.getName()) + MIMEType.WEBP.getFormat());
            avatar.transferTo(file);
            convertAvatarToWebp(file.toPath(), to);
            return avatarName;
        } catch (IOException e) {
            file.delete();
            new File(to.toUri()).delete();
            log.error("Can't save avatar: ", e);
            return null;
        }
    }

    public void deleteAvatar(Image image) {
        if (image.paths() != null) {
            image.paths().forEach(i -> {
                Path originalAvatarFolderPath = Paths.get(getAvatarImagesLocationPath(), "original");
                Path convertedAvatarFolderPath = Paths.get(getAvatarImagesLocationPath(), "webp");

                File[] files = new File(originalAvatarFolderPath.toUri()).listFiles((dir, n) -> n.startsWith(FilenameUtils.getBaseName(i.path())));
                File[] second = new File(convertedAvatarFolderPath.toUri()).listFiles((dir, n) -> n.startsWith(FilenameUtils.getBaseName(i.path())));

                File[] all = ArrayUtils.addAll(files, second);

                if (all != null) {
                    Arrays.stream(all).forEach(File::delete);
                }
            });
        }
    }

    private void convertAvatarToWebp(Path file, Path to) throws IOException {
        URL url = new URL(UriComponentsBuilder.fromUriString(webpPath + ":" + webpPort).build().toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setConnectTimeout(60000);

        ObjectMapper mapperObj = new ObjectMapper();
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("quality", "90");

        inputMap.put("file", file.toString());
        inputMap.put("to", to.toString());

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = mapperObj.writeValueAsString(inputMap).getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            if (con.getResponseCode() == 200) log.info(response.toString());
            else throw new IOException(response.toString());
        }
    }

    private String getAvatarImagesLocationPath() {
        return SystemUtils.IS_OS_WINDOWS
                ? windowsImagesPath + "avatar/"
                : linuxImagesPath + "avatar/";
    }
}

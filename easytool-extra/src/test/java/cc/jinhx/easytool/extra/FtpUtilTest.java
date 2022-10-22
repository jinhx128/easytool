package cc.jinhx.easytool.extra;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * FtpUtil单测
 *
 * @author jinhx
 * @since 2022-05-06
 */
@Slf4j
public class FtpUtilTest {

    @Test
    public void uploadFileTest() throws Exception {
        File file = new File("E:\\2.xlsx");
        InputStream inputStream = new FileInputStream(file);
        FtpUtil.uploadFile("", 21, "", "", "/usr/local",
                "/testfile/", "test.xlsx", inputStream);
    }

    @Test
    public void downloadFileTest() throws Exception {
        FtpUtil.downloadFile("", 21, "", "","/usr/local/testfile/",
                "test.csv", "/Users/ao/Desktop/test.csv");
    }

}

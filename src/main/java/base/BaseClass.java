package base;

import Utilities.POJO.Builder;
import Utilities.envPicker.Environment;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.io.File;

public class BaseClass {
	public static Environment envProperties;
	protected String env="";
	protected Builder requestBody = new Builder();

	@BeforeSuite()
	@Parameters({"environment"})
	@SneakyThrows
	public void initEnvironmentProperties(String envFromTestNG) {
		env=envFromTestNG;
		String propFilesLocation = "\\src\\main\\resources\\environment\\";
		File propertiesFile = new File(System.getProperty("user.dir") + propFilesLocation + envFromTestNG + ".properties");
		ConfigFactory.setProperty("file", String.valueOf(propertiesFile.toURI()));
		envProperties = ConfigFactory.create(Environment.class);
		env=envFromTestNG;

	}

	protected final RequestSpecification bookStoreRequestSpec() {
		return new RequestSpecBuilder()
					.setBaseUri(envProperties.BaseURI())
					.addHeader("accept", "application/json")
					.setBasePath("/v1")
					.addFilter(new AllureRestAssured())
					.build();
	}


	protected  final RequestSpecification petStoreRequestSpec() {
		return new RequestSpecBuilder()
					.setBaseUri(envProperties.BaseURI())
					.addHeader("accept", "application/json")
					.addHeader("Content-Type", "application/json")
					.setBasePath("/v2")
					.addFilter(new AllureRestAssured())
					.build();
	}

}


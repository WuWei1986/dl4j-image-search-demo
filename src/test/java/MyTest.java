import java.io.File;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.business.IImageSearchBusiness;
import com.example.entity.ProductExt;

public class MyTest extends BaseTest {

	@Autowired
	private IImageSearchBusiness imageSearchBusiness;
	
	@Test
	public void test(){
		List<ProductExt> productList = imageSearchBusiness.listProduct(new File("E:/deeplearning/fashionImage/train/2c48f743fe4d714f.jpg"));
		System.out.println(productList);
	}

}

package fi.ishtech.practice.oms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.ishtech.practice.oms.entity.SalesOrder;

/**
 *
 * @author Muneer Ahmed Syed
 */
@SpringBootTest
class SalesOrderMapperTests {

	@Autowired
	private SalesOrderMapper salesOrderMapper;

	@Test
	void toVoMapsTotalAmount() {
		var salesOrder = new SalesOrder();
		salesOrder.setOrigTotalAmount(new BigDecimal("350.00"));
		salesOrder.setTotalAmount(new BigDecimal("280.00"));
		salesOrder.setNetAmount(new BigDecimal("280.00"));

		assertThat(salesOrderMapper.toBriefVo(salesOrder).getTotalAmount()).isEqualByComparingTo("280.00");
		assertThat(salesOrderMapper.toSemiDetailVo(salesOrder).getTotalAmount()).isEqualByComparingTo("280.00");
		assertThat(salesOrderMapper.toDetailVo(salesOrder).getTotalAmount()).isEqualByComparingTo("280.00");
	}

}

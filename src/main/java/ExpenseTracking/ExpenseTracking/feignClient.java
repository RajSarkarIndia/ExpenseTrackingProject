package ExpenseTracking.ExpenseTracking;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="something",url="http://localhost:8080")
public interface feignClient {

}

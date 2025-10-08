package ExpenseTracking.ExpenseTracking.Controller;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import ExpenseTracking.ExpenseTracking.RoleBasedAuthSuccessHandler;
import ExpenseTracking.ExpenseTracking.feignClient;
import ExpenseTracking.ExpenseTracking.entity.Expenses;
import ExpenseTracking.ExpenseTracking.entity.ExpensesRepository;
import ExpenseTracking.ExpenseTracking.entity.User;
import ExpenseTracking.ExpenseTracking.entity.UserRepository;
import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MyController {

	@Autowired
	private UserRepository UserRepository;
	@Autowired
	private ExpensesRepository expensesRepository;
	@Autowired
	private PasswordEncoder PasswordEncoder;
	@Autowired
	private feignClient FeignClient;
	@Autowired
	private JavaMailSender javamail;

	@RequestMapping("/")
	public ModelAndView homeRedirectionIfLogin(Authentication authentication) {
		ModelAndView mav = new ModelAndView();

		if (authentication == null || !authentication.isAuthenticated()) {
			mav.setViewName("index");
			return mav;
		}

		var auths = authentication.getAuthorities();

		if (auths.contains(new SimpleGrantedAuthority("ROLE_manager")) || auths.contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
			mav.setViewName("redirect:/managerHome");
		} else if (auths.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			mav.setViewName("redirect:/admin/home");
		} else if (auths.contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))) {
			mav.setViewName("redirect:/expenses");
		} else {
			mav.setViewName("index");
		}
		return mav;
	}

	@PostMapping("/register")
	public ModelAndView register(@ModelAttribute User user) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("home");

		user.setPassword(PasswordEncoder.encode(user.getPassword()));

		UserRepository.save(user);

		// sending mail
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("Rajsarkar943592@gmail.com");
		message.setTo(user.getEmail());
		message.setSubject("Company Information of "+user.getUsername());
		message.setText("Thankyou for Registering with us here is your details of company\n" + user.getEmail()
				+ "\n Your Login Username " + user.getUsername());
		try {
			javamail.send(message);
		} catch (MailException ex) {

			System.err.println("Email send failed for " + user.getEmail() + ": " + ex.getMessage());

		}
		return mav;
	}

	@GetMapping("/admin/home")
	public ModelAndView adminHome(Principal principal) {
		ModelAndView mav = new ModelAndView();
		String company = principal.getName();
		int submitted = expensesRepository.sumOfSubmittedByCompany(company);
		int approved = expensesRepository.sumOfApprovedByCompany(company);
		int rejected = expensesRepository.sumOfRejectedByCompany(company);
		mav.addObject("submitted", submitted);
		mav.addObject("approved", approved);
		mav.addObject("reject", rejected);
		mav.setViewName("admin-home");
		return mav;
	}

	@GetMapping("/expenses")
	public ModelAndView listMyExpenses(Model model, Principal principal) {
		ModelAndView mav = new ModelAndView();
		String username = principal.getName();
		Optional<User> user = UserRepository.findByEmail(username);
		User userdetails = user.get();
		String company = userdetails.getCompany();
		mav.addObject("expenses", expensesRepository.findAllByUsername(username));
		// total amount submitted
		double SubmittedSum = expensesRepository.sumofSubmittedByUsername(username, company);
		mav.addObject("SubmittedSum", SubmittedSum);
		// total amount approved
		double ApprovedSum = expensesRepository.sumofApprovedByUsername(username, company);
		mav.addObject("ApprovedSum", ApprovedSum);
		mav.setViewName("employee-home");
		return mav;
	}

	@PostMapping("/insertExpenses")
	public ModelAndView insertExpenses(@ModelAttribute Expenses expenses, Principal principal) {
		ModelAndView mav = new ModelAndView();
		String username = principal.getName();
		expenses.setUsername(username);
		expenses.setStatus("Submitted");

		Optional<User> user = UserRepository.findByEmail(username);
		User userdetails = user.get();
		String company = userdetails.getCompany();
		expenses.setCompany(company);

		expensesRepository.save(expenses);
		mav.setViewName("redirect:/expenses");
		return mav;
	}

	@GetMapping("/managerhome")
	public ModelAndView expenseshome(Principal principal) {
		ModelAndView mav = new ModelAndView();
		String username = principal.getName();
		Optional<User> user = UserRepository.findByEmail(username);
		User userdetails = user.get();
		String company = userdetails.getCompany();
		// addObject of list for:- select row where status = submitted and approve or
		// reject it
		List<Expenses> list = expensesRepository.submittedList(company);
		mav.addObject("list", list);
		mav.setViewName("manager-home");
		return mav;
	}

	@PostMapping("/approve/{username}/{id}")
	public ModelAndView approve(@PathVariable String username, @PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		int done = expensesRepository.approveByUsername(username, id);
		mav.setViewName("redirect:/managerhome");
		return mav;
	}

	@PostMapping("/reject/{username}/{id}")
	public ModelAndView reject(@PathVariable String username, @PathVariable int id) {
		ModelAndView mav = new ModelAndView();
		int done = expensesRepository.rejectByUsername(username, id);
		mav.setViewName("redirect:/managerhome");
		return mav;
	}

}

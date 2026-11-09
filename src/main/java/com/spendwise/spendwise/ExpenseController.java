
package com.spendwise.spendwise;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/expenses/new")
    public String showExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "expense-form";
    }

    @PostMapping("/expenses")
    public String saveExpense(
            @ModelAttribute Expense expense,
            RedirectAttributes redirectAttributes) {

        expenseRepository.save(expense);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Expense saved successfully!");

        return "redirect:/expenses/new";
    }

    @GetMapping("/expenses")
public String showExpenses(Model model) {
    model.addAttribute("expenses", expenseRepository.findAll());
    return "expenses";
}
}

package org.utj.asman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.utj.asman.model.County;
import org.utj.asman.service.CountyService;

@Controller
@RequestMapping("/admin/counties")
public class CountyController {

    @Autowired
    private CountyService countyService;

    @GetMapping
    public String listCounties(Model model) {
        model.addAttribute("counties", countyService.getAllCounties());
        model.addAttribute("newCounty", new County());
        return "admin/county_list";
    }

    @PostMapping("/save")
    public String saveCounty(@ModelAttribute County county) {
        countyService.saveCounty(county);
        return "redirect:/admin/counties";
    }

    @PostMapping("/delete/{id}")
    public String deleteCounty(@PathVariable Long id) {
        countyService.deleteCounty(id);
        return "redirect:/admin/counties";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public County getCounty(@PathVariable Long id) {
        return countyService.getCountyById(id)
                .orElseThrow(() -> new RuntimeException("County not found"));
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public County updateCounty(@PathVariable Long id, @RequestBody County county) {
        County existing = countyService.getCountyById(id)
                .orElseThrow(() -> new RuntimeException("County not found"));
        existing.setCountyName(county.getCountyName());
        existing.setCountyCode(county.getCountyCode());
        return countyService.saveCounty(existing);
    }
}

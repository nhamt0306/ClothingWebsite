package com.example.clothingstore.controller;


import com.example.clothingstore.config.mapper.StatisticMapper;
import com.example.clothingstore.dto.DateForm;
import com.example.clothingstore.dto.ProductInMonthDTO;
import com.example.clothingstore.dto.RevenueFrom2Day;
import com.example.clothingstore.model.TransactionEntity;
import com.example.clothingstore.model.TypeEntity;
import com.example.clothingstore.service.impl.OrderServiceImpl;
import com.example.clothingstore.service.impl.ProductServiceImpl;
import com.example.clothingstore.service.impl.TransactionServiceImpl;
import com.example.clothingstore.service.impl.TypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin/statistic")
public class StatisticController {
    @Autowired
    OrderServiceImpl orderService;
    @Autowired
    TypeServiceImpl typeService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    TransactionServiceImpl transactionService;

    @GetMapping("/year")
    public String getStatisticByYear(){
        String res = orderService.getAllOrderByYear();
        if (res == null)
        {
            return "0";
        }
        return res;
    }

    @GetMapping("/month")
    public String getStatisticByMonth(){
        String res = orderService.getAllOrderByMonth();
        if (res == null)
        {
            return "Turnover: 0";
        }
        return res;
    }

    @GetMapping("/day")
    public String getStatisticByDay(){
        String res = orderService.getAllOrderByDay();
        if (res == null)
        {
            return "0";
        }
        return res;
    }

    @GetMapping("/product-chart")
    public Object productChartInMonth(){
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        List<TypeEntity> res = typeService.statisticProductInMonth(year);
        if (res == null)
        {
            return "0";
        }
        List<ProductInMonthDTO> product = new ArrayList<>();
        for(TypeEntity type : res){
            ProductInMonthDTO productInMonthDTO = new ProductInMonthDTO();
            productInMonthDTO.setSold(type.getSold());
            productInMonthDTO.setName(type.getProductEntity().getName());
            product.add(productInMonthDTO);
        }
        return product;
    }

    @GetMapping("/chart")
    public Object getStatisticByDate(){
        Date date = new Date(); // current date
        List<StatisticMapper> chartList= new ArrayList<>(); // map return
        for (int i =0; i< 10; i++)
        {
            Date yesterday = new Date(date.getTime() - (1000*60*60*24*i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(yesterday);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) +1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (orderService.getAllOrderByDate(year, month, day) == null)
            {
                chartList.add(new StatisticMapper(String.valueOf(day)+"/"+String.valueOf(month), 0));
            }
            else {
                StatisticMapper statisticMapper = new StatisticMapper(day+"/"+month,(orderService.getAllOrderByDate(year, month, day)));
                chartList.add(statisticMapper);
            }
        }
        Collections.reverse(chartList);
        return chartList;
    }

    @GetMapping("/chart-between")
    public Object getStatisticBy2Date(@RequestBody RevenueFrom2Day revenueFrom2Day){
        Date date = new Date(); // current date
        // cal two day
        Instant instant1 = Instant.parse(revenueFrom2Day.getFrom().toString());
        Instant instant2 = Instant.parse(revenueFrom2Day.getTo().toString());
        Duration duration = Duration.between(instant1, instant2);
        Long hours = duration.toDays();
        List<StatisticMapper> chartList= new ArrayList<>(); // map return
        for (int i =0; i< Integer.parseInt(hours.toString()); i++)
        {
            Date yesterday = new Date(date.getTime() - (1000*60*60*24*i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(yesterday);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) +1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (orderService.getAllOrderByDate(year, month, day) == null)
            {
                chartList.add(new StatisticMapper(String.valueOf(day)+"/"+String.valueOf(month), 0));
            }
            else {
                StatisticMapper statisticMapper = new StatisticMapper(day+"/"+month,(orderService.getAllOrderByDate(year, month, day)));
                chartList.add(statisticMapper);
            }
        }
        Collections.reverse(chartList);
        return chartList;
    }



    @GetMapping("/chart-between-2-day")
    public Object getStatisticBetween2Date(@RequestBody DateForm endDate) throws ParseException {
        Date date = endDate.getEndDate(); // get end date
        List<StatisticMapper> chartList= new ArrayList<>(); // map return
        for (int i =0; i< 10; i++)
        {
            Date yesterday = new Date(date.getTime() - (1000*60*60*24*i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(yesterday);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) +1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (orderService.getAllOrderByDate(year, month, day) == null)
            {
                chartList.add(new StatisticMapper(String.valueOf(day)+"/"+String.valueOf(month), 0));
            }
            else {
                StatisticMapper statisticMapper = new StatisticMapper(day+"/"+month,(orderService.getAllOrderByDate(year, month, day)));
                chartList.add(statisticMapper);
            }
        }
        Collections.reverse(chartList);
        return chartList;
    }

    // top product chart between 2 day
    @GetMapping("/top-product-between-2-day")
    public Object productChartBeetween2Day(@RequestBody RevenueFrom2Day revenueFrom2Day){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String from = formatter.format(revenueFrom2Day.getFrom());
//        String to = formatter.format(revenueFrom2Day.getTo());
        List<TransactionEntity> res = transactionService.topProductBetween2Day(revenueFrom2Day.getFrom(), revenueFrom2Day.getTo());
        if (res == null)
        {
            return "0";
        }
        List<ProductInMonthDTO> product = new ArrayList<>();
        for(TransactionEntity transactionEntity : res){
            ProductInMonthDTO productInMonthDTO = new ProductInMonthDTO();
            productInMonthDTO.setSold(transactionEntity.getQuantity());
            productInMonthDTO.setName(transactionEntity.getProductEntity().getName());
            product.add(productInMonthDTO);
        }
        return product;
    }

}

package com.library.seat.controller;

import com.library.seat.entity.Reservation;
import com.library.seat.entity.User;
import com.library.seat.service.ReservationService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/reservation")
    public void exportReservation(HttpServletResponse response) {
        User admin = currentUser();
        if (admin == null || !"admin".equals(admin.getRole())) {
            response.setStatus(403);
            return;
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            List<Reservation> list = reservationService.getAllList();
            Sheet sheet = workbook.createSheet("预约数据");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"预约编号", "用户", "自习室", "座位", "开始时间", "结束时间", "使用状态", "审核状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                sheet.setColumnWidth(i, 18 * 256);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (Reservation reservation : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(value(reservation.getReservationNo()));
                row.createCell(1).setCellValue(value(reservation.getUserName()));
                row.createCell(2).setCellValue(value(reservation.getRoomName()));
                row.createCell(3).setCellValue(value(reservation.getSeatCode()));
                row.createCell(4).setCellValue(reservation.getStartTime() == null ? "" : reservation.getStartTime().format(formatter));
                row.createCell(5).setCellValue(reservation.getEndTime() == null ? "" : reservation.getEndTime().format(formatter));
                row.createCell(6).setCellValue(value(reservation.getStatus()));
                row.createCell(7).setCellValue(value(reservation.getAuditStatus()));
            }

            String filename = "预约数据_" + System.currentTimeMillis() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8.name()));
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }
}

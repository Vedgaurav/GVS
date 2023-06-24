package com.voice.yatraRegistration.accomodationReg.restController;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.apache.bcel.classfile.Constant;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.model.RoomSet;
import com.voice.yatraRegistration.accomodationReg.model.RoomType;
import com.voice.yatraRegistration.accomodationReg.service.AsyncService;
import com.voice.yatraRegistration.accomodationReg.service.RoomBookingService;
import com.voice.yatraRegistration.accomodationReg.utils.Constants;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.model.Member;

@RestController
@RequestMapping("/v1/room/bookings/")
@CrossOrigin("*")
@EnableAsync
public class RoomBookingController {

    @Autowired
    RoomBookingDao bookingDao;

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Autowired
    RoomDao roomDao;

    @Autowired
    MemberDao memberDao;

    @Autowired
    AsyncService asyncService;

    @Autowired
    RoomBookingService roomBookingService;

    @PostMapping("/fetchAll")
    public List<RoomBooking> fetchAllBookings() {
        return bookingDao.findAll();
    }

    @PostMapping("/saveBooking")
    public RoomBooking saveRoom(@RequestBody RoomBooking booking) {
        return bookingDao.save(booking);
    }

    @PostMapping("/reserveRoomAndProceedForPayment")
    public Long reserveRoomAndProceedForPayment(@RequestBody RoomBooking booking) {
        // calculate amount
        try {
            String amount = roomBookingService.validateCountAndCalculateAmount(booking.getRoomSet());

            // @Transaction
            // check room count and give error if not found

            // save in db with INITIATED state
            booking.setAmount(amount);
            booking.setUpiTxnId(UUID.randomUUID().toString()); //temp value
            booking.setPaymentStatus(Constants.INITIATED);
            RoomBooking bookedRoom = bookingDao.save(booking);

            // decrease the count
            roomBookingService.manageRoomCount(booking.getRoomSet(), false);
            // @Transaction

            asyncService.waitAsync(bookedRoom.getId());

            System.out.println("Booking reserved for 5min. Please proceed for txn.");
            return bookedRoom.getId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @PostMapping("/saveTxn")
    public RoomBooking saveTxnDetail(@RequestBody Map<String, String> req) {

        String bookingId = req.get("bookingId");
        // transaction
        String customerTxnId = "";
        String customerVPA = "";
        String customerEmail = "";
        String upiTxnId = req.get("txnId");
        String txnDate = String.valueOf(LocalDateTime.now());

        if (bookingId == null) {
            System.out.println("Id must be there");
            return null;
        }

        if (upiTxnId == null) {
            System.out.println("txn Id not found");
            return null;
        }

        RoomBooking rm = bookingDao.findOneById(Long.parseLong(bookingId));

        if (rm.getPaymentStatus().equals(Constants.TIMEOUT)) {
            // TODO - error
            return null;
        }

        rm.setUpiTxnId(upiTxnId);
        rm.setTxnDate(txnDate);
        rm.setPaymentStatus(Constants.PENDING);

        RoomBooking res = bookingDao.save(rm);
        return res;
    }

    @PostMapping("/fetchAllPendingBookings")
    public List<RoomBooking> getAllPendingBookings(){
        return bookingDao.findAllByPaymentStatus(Constants.PENDING);
    }

     @PostMapping("/approve/{id}")
    public RoomBooking approveBooking(@PathVariable("id") Long roomBookingId){
        RoomBooking rm = bookingDao.findOneById(roomBookingId);
        rm.setPaymentStatus(Constants.APPROVED);
        return bookingDao.save(rm);
    }

     @PostMapping("/decline/{id}")
    public RoomBooking declineBooking(@PathVariable("id") Long roomBookingId){
        RoomBooking rm = bookingDao.findOneById(roomBookingId);

        //increase the room count
        roomBookingService.manageRoomCount(rm.getRoomSet(), true);

        //set status as decline
        rm.setPaymentStatus(Constants.DECLINE);
        return bookingDao.save(rm);
    }

}

//package com.voice.yatraRegistration.memberReg.utils.common;
//
//import java.io.Serializable;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.Year;
//
//import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SharedSessionContractImplementor;
//import org.hibernate.id.IdentifierGenerator;
//
//public class YatraMemRegIdGenerator implements IdentifierGenerator {
//
//    @Override
//    public Serializable generate(SharedSessionContractImplementor session, Object object)
//            throws HibernateException {
//
//        String prefix = "YMEM";
//        Connection connection = session.connection();
//        int year = Year.now().getValue()%100;
//
//        try {
//            Statement statement=connection.createStatement();
//
//            ResultSet rs=statement.executeQuery("select count(*) from yatra_aug_23");
//
//            if(rs.next())
//            {
//                int id=rs.getInt(1)+10;
//                String generatedId = prefix +String.valueOf(year)+ String.valueOf(id);
//                return generatedId;
//            }
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    }
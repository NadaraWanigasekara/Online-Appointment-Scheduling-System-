package com.example.mywebapp;

public class BubbleSort {
    public static void sortByTime(AdminManageAppointment[] appointments, int size) {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (appointments[j].getTimeInMinutes() > appointments[j + 1].getTimeInMinutes()) {
                    // Swap appointments
                    AdminManageAppointment temp = appointments[j];
                    appointments[j] = appointments[j + 1];
                    appointments[j + 1] = temp;
                }
            }
        }
    }
}


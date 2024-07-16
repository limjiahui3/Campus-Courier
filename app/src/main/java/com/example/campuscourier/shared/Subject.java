package com.example.campuscourier.shared;

import com.example.campuscourier.shared.Observer;

public interface Subject {
    void register(Observer o);
    void downloadToObserver();
}

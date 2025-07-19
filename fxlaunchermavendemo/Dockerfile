FROM alpine
RUN     apk add doas; \
        adduser juan; \
        echo 'juan:juan' | chpasswd; \
        echo 'permit juan as root' > /etc/doas.d/doas.conf
USER juan

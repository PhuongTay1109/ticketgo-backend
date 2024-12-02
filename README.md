SHOW VARIABLES LIKE 'event_scheduler';
SET GLOBAL event_scheduler = ON;

ALTER EVENT reset_booking_status DISABLE;
ALTER EVENT reset_status_tickets DISABLE;
ALTER EVENT reset_ticket_info DISABLE;

CREATE EVENT reset_booking_status
ON SCHEDULE EVERY 1 day
DO
    UPDATE bookings
    SET status = 'FAILED'
    WHERE TIMESTAMPDIFF(MINUTE, booking_date, NOW()) >= 15 
    AND status = 'IN_PROGRESS';

CREATE EVENT reset_ticket_info
ON SCHEDULE EVERY 10 SECOND
DO
    UPDATE tickets
    SET status = 'AVAILABLE', booking_id = NULL, customer_id = NULL
    WHERE booking_id IN (
        SELECT booking_id
        FROM bookings
        WHERE TIMESTAMPDIFF(MINUTE, booking_date, NOW()) >= 15 
        AND status = 'IN_PROGRESS'
    );

CREATE EVENT `reset_status_tickets`
ON SCHEDULE EVERY 1 SECOND
DO
UPDATE tickets
  SET 
    status = 'AVAILABLE',
    reserved_until = NULL,
    customer_id = NULL
  WHERE reserved_until < NOW();

-- Migration script to make serial_number nullable in laptop table
-- Run this script on your database to fix the constraint issue

ALTER TABLE `laptop` MODIFY COLUMN `serial_number` VARCHAR(255) NULL;








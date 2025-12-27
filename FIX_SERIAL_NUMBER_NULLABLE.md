# Fix: Make serial_number Column Nullable

The `serial_number` column in the `laptop` table has a NOT NULL constraint that needs to be removed.

## Quick Fix - Run this SQL command:

Connect to your MySQL database and run:

```sql
ALTER TABLE `laptop` MODIFY COLUMN `serial_number` VARCHAR(255) NULL;
```

## How to run:

### Option 1: Using MySQL Command Line
```bash
mysql -u root -p securecapita
```
Then paste the ALTER TABLE command above.

### Option 2: Using MySQL Workbench or any SQL client
1. Connect to your database
2. Select the `securecapita` database
3. Run the ALTER TABLE command

### Option 3: Using the migration script
```bash
mysql -u root -p securecapita < migration_alter_laptop_serial_number.sql
```

## After running the migration:

Once the column is nullable, you can remove the workaround in `LaptopService.java` that sets `serialNumber` to an empty string, and it will properly allow null values.







DROP TABLE IF EXISTS `funkos`;
CREATE TABLE IF NOT EXISTS `funkos` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `myid` LONG NOT NULL,
    `cod` UUID NOT NULL,
    `nombre` VARCHAR(255),
    `modelo` ENUM('MARVEL', 'DISNEY', 'ANIME', 'OTROS'),
    `precio` DECIMAL(10,2),
    `fecha_lanzamiento` TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
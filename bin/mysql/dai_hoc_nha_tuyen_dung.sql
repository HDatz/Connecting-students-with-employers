-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dai_hoc
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `nha_tuyen_dung`
--

DROP TABLE IF EXISTS `nha_tuyen_dung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nha_tuyen_dung` (
  `id_nha_tuyen_dung` int NOT NULL AUTO_INCREMENT,
  `ten_cong_ty` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mat_khau` varchar(255) NOT NULL,
  `so_dien_thoai` varchar(255) DEFAULT NULL,
  `dia_chi` varchar(255) DEFAULT NULL,
  `linh_vuc` varchar(255) DEFAULT NULL,
  `trang_web` varchar(255) DEFAULT NULL,
  `mo_ta_cong_ty` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `role` enum('ROLE_NHATUYENDUNG') NOT NULL DEFAULT 'ROLE_NHATUYENDUNG',
  PRIMARY KEY (`id_nha_tuyen_dung`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `so_dien_thoai` (`so_dien_thoai`),
  KEY `idx_email_nha_tuyen_dung` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nha_tuyen_dung`
--

LOCK TABLES `nha_tuyen_dung` WRITE;
/*!40000 ALTER TABLE `nha_tuyen_dung` DISABLE KEYS */;
INSERT INTO `nha_tuyen_dung` VALUES (2,'IPOS','iposvn@gmail.com','$2a$10$iw0BqopmfWriQFoLklPx5ulgmHPJvSjUbkC7yBr.Y2u2fzDHWO8S.','0914162689','Hà Nội','CNTT','https://ipos.vn/','','nhatuyendung_iposvn@gmail.com_123.png',NULL,'ROLE_NHATUYENDUNG'),(3,'CÔNG TY TNHH XÂY DỰNG VÀ SẢN XUẤT NHÔM KÍNH VIỆT CHIẾN','xaydung@gmail.com','$2a$10$0UYJKbmug8KXODPiek/w4.t1JG.WxwHcZoQEZ8Vz18IclbU5lqyVq','0986867870','Số 116, Đường 428 Thôn Đồng Xung, Xã Đồng Tân, Huyện Ứng Hoà, Thành phố Hà Nội','Xây đựng','https://noithatnhiha.com/','Công Ty Cổ Phần Xây Dựng Và Nội Thất Nhị Hà là nhà thầu nhôm kính đã khẳng định được sự uy tín và chuyên nghiệp trên thị trường','nhatuyendung_xaydung@gmail.com_1234.png',NULL,'ROLE_NHATUYENDUNG');
/*!40000 ALTER TABLE `nha_tuyen_dung` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-03 13:46:37

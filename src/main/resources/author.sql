SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for author
-- ----------------------------
DROP TABLE IF EXISTS `author`;
CREATE TABLE `author`  (
                           `id` int NOT NULL COMMENT '主键',
                           `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                           `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                           `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                           `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务字段',
                           `favourite_section` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收藏夹',
                           `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '作者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of author
-- ----------------------------
INSERT INTO `author` VALUES (1, '张三', 'zhangmima', 'a@b.cc', '1', '1111', '2021-01-01 01:01:01');
INSERT INTO `author` VALUES (2, '李四', 'LEEEEEE', 'leeeee@shi.net', '2', '222', '2021-02-02 02:02:02');
INSERT INTO `author` VALUES (3, '王五', 'w5555555', 'wang@wu.cc', '3', '33', NULL);
INSERT INTO `author` VALUES (4, '赵六', 'z6666666', 'zhao@liu.cc', '4', '44', '2021-04-04 04:44:44');

SET FOREIGN_KEY_CHECKS = 1;

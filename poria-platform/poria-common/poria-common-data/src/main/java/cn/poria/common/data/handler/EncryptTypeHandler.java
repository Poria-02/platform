package cn.poria.common.data.handler;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import cn.poria.common.data.util.EncryptTypeUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 敏感数据加密(例身份证)
 * 实体类添加@TableName(value = "表名",autoResultMap = true)
 * 加密字段上添加 @TableField(typeHandler = EncryptTypeHandler.class)
 * 使用sql查询时需要对加密字段映射配置 例如：添加 gender 字段的映射配置 -->
 *  <result column="idCard" property="idCard" jdbcType="INTEGER" typeHandler="cn.poria.common.data.handler.EncryptTypeHandler"/>
 * @return
 * @throws IOException
 */
public class EncryptTypeHandler implements TypeHandler {
    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, EncryptTypeUtil.setResultEncrypt(parameter));
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        String encryptedValue = rs.getString(columnName);
        return  EncryptTypeUtil.getResultDecrypt(encryptedValue);

    }


    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        String encryptedValue = rs.getString(columnIndex);
        return EncryptTypeUtil.getResultDecrypt(encryptedValue);
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encryptedValue = cs.getString(columnIndex);
        return EncryptTypeUtil.getResultDecrypt(encryptedValue);
    }


}
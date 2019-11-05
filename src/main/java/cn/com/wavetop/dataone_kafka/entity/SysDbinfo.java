package cn.com.wavetop.dataone_kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author yongz
 * @Date 2019/10/10、11:45
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysDbinfo {

  private Long id;
  private String host;
  private String user;
  private String password;
  private String name;
  private String dbname;
  private String schema;
  private Long port;
  private Long sourDest;
  private Long type;

}

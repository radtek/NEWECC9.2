package COM.dragonflow.Properties;

import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * API2.1����:f.php?phone=xxxxxx&pwd=xxx&to=xxxx&msg=xxxx&type=0
 *���Ͻӿڲ�����ϸ˵�� VIP API
 *1.phone:�ֻ���
 *2.pwd:��������
 *3.to:���͸�˭(�ֻ��Ż���ź�)
 *4.msg:��������
 *5.type:���� 0(��)���Ͷ��� 1������ 2���Ӻ���
 *6.u:���ò���:����������Ϊ����ʱ ��������&u=1
 */
public class Fetion {
	
	public static void sendMsg(String _phone,String _pwd,String _to,String _msg) throws HttpException, IOException{
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://3.ibtf.sinaapp.com/f.php");
		post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");//��ͷ�ļ�������ת��
		NameValuePair[] data ={ 
				new NameValuePair("phone", _phone),
				new NameValuePair("pwd", _pwd),
				new NameValuePair("to",_to),
				new NameValuePair("msg",_msg),
				new NameValuePair("type","0")
				};
		post.setRequestBody(data);
	
		client.executeMethod(post);
		Header[] headers = post.getResponseHeaders();
		int statusCode = post.getStatusCode();
		System.out.println("statusCode:"+statusCode);
		for(Header h : headers){
			System.out.println(h.toString());
		}
		//String result = new String(post.getResponseBodyAsString().getBytes("utf-8"));
		//System.out.println(result);
		System.out.println("ok!");
		post.releaseConnection();
	}
}
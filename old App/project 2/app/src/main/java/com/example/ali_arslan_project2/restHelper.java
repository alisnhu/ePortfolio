package com.example.ali_arslan_project2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import com.example.ali_arslan_project2.*;
import org.json.JSONObject;


public class restHelper
{
	
	private String url;
	private URL connURL;
	private HttpURLConnection conn;
	private final procedures myp = new procedures();
	private Map<String , Object> rv = new HashMap<String, Object>();
	
	
	public restHelper(String url) 
	{
		if(url== null || url == "") 
		{
			throw new IllegalArgumentException("Argument can not be null");
		}
		else 
		{
			this.url = url;
			if(this.url.charAt(this.url.length()-1)!='/') 
			{
				this.url += "/";
			}
		}
	}

	public int setURL(String extension,String method,JSONObject jsonParam) throws Exception 	
	{
		int rv =0;
		boolean output = false;
		boolean body = false;
		try 
		{
			if(method=="GET") 
			{
				if(jsonParam!=null) 
				{
					int i = 0;
					for (String key : jsonParam.keySet()) 
					{
						if(i==0) 
						{
							extension += "?"+key+"="+jsonParam.get(key);
						}
						else 
						{

							extension += "&"+key+"="+jsonParam.get(key);
						}
						i++;
			        }
				}
			}
			else 
			{
				output = true;
				body = true;
			}
			this.connURL = new URI(this.url+extension).toURL();
			this.conn = (HttpURLConnection) connURL.openConnection();
			this.conn.setRequestProperty("Content-Type", "application/json");
			if(method=="PATCH") 
			{
				this.conn.setRequestMethod(myp.post);
				this.conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			}
			else 
			{
				this.conn.setRequestMethod(method);
			}
			this.conn.setRequestProperty("Accept", "application/json");
			if(body) 
			{

				this.conn.setDoOutput(output); 
				try (OutputStream os = this.conn.getOutputStream()) 
				{
	                byte[] input = jsonParam.toString().getBytes("utf-8");
	                os.write(input, 0, input.length);  
	            }
			}
			
			
			rv = this.conn.getResponseCode();
		}
		catch(Exception e) 
		{
			throw new Exception("Http Connection could not started : "  + e.getMessage());
			
		}
		return rv;
	}
	
	private String getData() 
	{
		String rv = null;
        try 
        {
    		InputStreamReader inputStreamReader = new InputStreamReader(this.conn.getInputStream());
            BufferedReader in = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
			rv = response.toString();
		} 
        catch (IOException e) 
        {
		}
        return rv;
	}
	
	private Map<String , Object> stringToMap(String Data)
	{
		Map<String,Object> rv = new HashMap<String,Object>();
		JSONObject myObject = new JSONObject(Data);
		rv.put("status", myObject.getBoolean("status"));
		rv.put("message", myObject.getString("message"));
		rv.put("result", myObject.get("result"));
		return rv;
	}
	
	public Map<String, Object> getScheme() 
	{
		
		try
		{
			int response = this.setURL(myp.scheme,myp.get,null);
			if(response ==200 || response == 201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
		
	}
	
	public Map<String,Object> Login(String username , String password)
	{
		try 
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("username", username);
			jsonParam.put("password", password);
			int response = this.setURL(myp.user,myp.get,jsonParam);
			
			if(response ==  200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}			
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	
	public Map<String,Object> signUp(String username, String password, String email, String phone)
	{
	 
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("username", username);
			jsonParam.put("password", password);
			jsonParam.put("email", email);
			jsonParam.put("phone", phone);
			int response = this.setURL(myp.user,myp.put,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	
	
	
	
	
	
	public Map<String,Object> getInventory(String inventoryId, String sessionToken)
	{
		try 
		{
			String add = "";
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("sessionToken", sessionToken);
			if(inventoryId!="" && inventoryId!=null) 
			{
				add="/"+inventoryId;
			}
			int response = this.setURL(myp.inventory+add,myp.get,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
		}
		catch(Exception e) 
		{

			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		
		return this.rv;
	}
	public Map<String, Object> addInventory(String inventoryName ,  String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("inventoryName", inventoryName);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.inventory,myp.post,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String, Object> deleteInventory(String inventoryId ,  String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("inventoryID", inventoryId);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.inventory,myp.delete,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String, Object> updateInventory(String inventoryName ,String inventoryID ,  String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("inventoryID", inventoryID);
			jsonParam.put("inventoryName", inventoryName);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.inventory,myp.patch,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}

	
	
	public Map<String, Object> getSubuser(String id, String sessionToken)
	{
		try 
		{
			String add = "";
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("sessionToken", sessionToken);
			if(id!="" && id!=null) 
			{
				add="/"+id;
			}
			int response = this.setURL(myp.subuser+add,myp.get,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
		}
		catch(Exception e) 
		{

			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		
		return this.rv;
	}
	public Map<String, Object> addSubuser(String name  ,String passsword, String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("username", name);
			jsonParam.put("password", passsword);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.subuser,myp.post,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String, Object> deleteSubuser(String id ,  String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("userid", id);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.subuser,myp.delete,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String, Object> updateSubuser(String name ,String passsword , String id ,  String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("userid", id);
			jsonParam.put("password", passsword);
			jsonParam.put("username", name);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.subuser,myp.put,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}

	
	public Map<String, Object> addItem(String name  ,String location,String stock,String inventoryId, boolean notification, int less , String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name", name);
			jsonParam.put("location", location);
			jsonParam.put("stock", stock);
			jsonParam.put("notification", notification);
			jsonParam.put("inventoryId", inventoryId);
			jsonParam.put("less", less);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.item,myp.post,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String,Object> getItem(String Id,String inventoryId, String sessionToken)
	{
		try 
		{
			String add = "";
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("inventoryId", inventoryId);
			jsonParam.put("sessionToken", sessionToken);
			if(Id!="" && Id!=null) 
			{
				add="/"+Id;
			}
			int response = this.setURL(myp.item+add,myp.get,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
		}
		catch(Exception e) 
		{

			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		return this.rv;
	}
	public Map<String, Object> deleteItem(String id ,  String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("id", id);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.item,myp.delete,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String, Object> updateItem(String id, String name  ,String location,String stock,String inventoryId, boolean notification, int less , String sessionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("id", id);
			jsonParam.put("name", name);
			jsonParam.put("location", location);
			jsonParam.put("stock", stock);
			jsonParam.put("notification", notification);
			jsonParam.put("inventoryId", inventoryId);
			jsonParam.put("less", less);
			jsonParam.put("sessionToken", sessionToken);
			int response = this.setURL(myp.item,myp.put,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	
	
	public Map<String, Object> addNotification(String name , int lessThan ,int inventoryId, String sesionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name", name);
			jsonParam.put("less", lessThan);
			jsonParam.put("inventoryId", inventoryId);
			jsonParam.put("sessionToken", sesionToken);
			int response = this.setURL(myp.notification,myp.post,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	public Map<String, Object> deleteNotification(int id , String sesionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("id", id);
			jsonParam.put("sessionToken", sesionToken);
			int response = this.setURL(myp.notification,myp.delete,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
	
	public Map<String,Object> getNotification(String Id,int inventoryId, String sessionToken)
	{
		try 
		{
			String add = "";
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("inventoryId", inventoryId);
			jsonParam.put("sessionToken", sessionToken);
			if(Id!="" && Id!=null) 
			{
				add="/"+Id;
			}
			int response = this.setURL(myp.notification+add,myp.get,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
		}
		catch(Exception e) 
		{

			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		return this.rv;
	}
	
	public Map<String, Object> updateNotification(String id,String name , int lessThan ,int inventoryId, String sesionToken)
	{
		try
		{
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("id", id);
			jsonParam.put("name", name);
			jsonParam.put("less", lessThan);
			jsonParam.put("inventoryId", inventoryId);
			jsonParam.put("sessionToken", sesionToken);
			int response = this.setURL(myp.notification,myp.put,jsonParam);
			if(response ==200 || response==201) 
			{
				String jsonData = this.getData();
				if(jsonData==null) 
				{
					this.rv.put("status",false);
					this.rv.put("message","Data Could not retrieved");
					this.rv.put("result", null);
				}
				else 
				{
					this.rv=this.stringToMap(jsonData);
				}
			}
			else 
			{
				this.rv.put("status",false);
				this.rv.put("message","Server Return Http Code : "+response);
				this.rv.put("result", null);
			}
		}
		catch(Exception e) 
		{
			this.rv.put("status",false);
			this.rv.put("message",e.getMessage());
			this.rv.put("result", null);
		}
		
		return this.rv;
	}
}

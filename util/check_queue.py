import pika

credentials = pika.PlainCredentials('guest', 'guest')
parameters = pika.ConnectionParameters('localhost', 5672, '/', credentials)

try:
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    
    # Declare the queue passive=True to inspect it without modifying/creating it
    queue_info = channel.queue_declare(queue='cola.mails', passive=True)
    message_count = queue_info.method.message_count
    consumer_count = queue_info.method.consumer_count
    
    print(f"QUEUE_STATUS:cola.mails:messages={message_count}:consumers={consumer_count}")
    
    # Let's also check cola.auditoria
    try:
        queue_info_audit = channel.queue_declare(queue='cola.auditoria', passive=True)
        print(f"QUEUE_STATUS:cola.auditoria:messages={queue_info_audit.method.message_count}:consumers={queue_info_audit.method.consumer_count}")
    except Exception as e:
        print(f"QUEUE_STATUS:cola.auditoria:error={e}")
        
    connection.close()
except Exception as e:
    print(f"ERROR: {e}")

using Confluent.Kafka;
using Microsoft.AspNetCore.Mvc;
using System.Threading;
using System.Threading.Tasks;
using System;
using Newtonsoft.Json;


namespace WebApplicationApache.Controllers
{
    public class KafkaController : Controller
    {
        private readonly IKafkaProducerService _kafkaProducer;
        private readonly IKafkaConsumerService _kafkaConsumer;

        public KafkaController(IKafkaProducerService kafkaProducer, IKafkaConsumerService kafkaConsumer)
        {
            _kafkaProducer = kafkaProducer;
            _kafkaConsumer = kafkaConsumer;
        }
        [HttpGet]
        public IActionResult Index()
        {
            return View();
        }
        [HttpPost]
        public async Task<IActionResult> SendMessage(string message, string service = "concatena_string")
        {
            var jsonResponse = new
            {
                //identifier = "A",
                service = service,
                body = message
            };

            string jsonString = JsonConvert.SerializeObject(jsonResponse);
            await _kafkaProducer.ProduceAsync("request-topic", jsonString);
            return Json(true);
        }

        [HttpGet]
        public async Task<IActionResult> ConsumeMessages(CancellationToken cancellationToken)
        {
            string message = await _kafkaConsumer.ConsumeAsync(cancellationToken);

            var jsonObject = JsonConvert.DeserializeObject<Dictionary<string, string>>(message);

            string messageBody = jsonObject["body"];

            return Json(new { message = messageBody });

        }
    }
    

    public interface IKafkaProducerService
    {
        Task ProduceAsync(string topic, string message);
    }

    public class KafkaProducerService : IKafkaProducerService
    {
        private readonly IProducer<Null, string> _producer;

        public KafkaProducerService(IProducer<Null, string> producer)
        {
            _producer = producer;
        }

        public async Task ProduceAsync(string topic, string message)
        {
            await _producer.ProduceAsync(topic, new Message<Null, string> { Value = message });
        }
    }
    

public interface IKafkaConsumerService
    {
        Task<string> ConsumeAsync(CancellationToken cancellationToken);
    }

    public class KafkaConsumerService : IKafkaConsumerService, IHostedService
    {
        private readonly IConsumer<Ignore, string> _consumer;
        private readonly string _topic;

        public KafkaConsumerService(IConsumer<Ignore, string> consumer, string topic)
        {
            _consumer = consumer;
            _topic = topic;
        }

        public Task StartAsync(CancellationToken cancellationToken)
        {
            return Task.Run(() => ConsumeAsync(cancellationToken), cancellationToken);
        }

        public async Task<string?> ConsumeAsync(CancellationToken cancellationToken)
        {
            _consumer.Subscribe(_topic);

            try
            {
                while (!cancellationToken.IsCancellationRequested)
                {
                    var consumeResult = _consumer.Consume(cancellationToken);
                    if (consumeResult != null)
                    {
                        Console.WriteLine($"Received message: {consumeResult.Message.Value}");
                        return consumeResult.Message.Value;
                    }
                }
            }
            catch (OperationCanceledException)
            {
                _consumer.Close();
            }

            return null;
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            _consumer.Unsubscribe();
            _consumer.Close();
            return Task.CompletedTask;
        }
    }


}


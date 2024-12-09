using Confluent.Kafka;
using WebApplicationApache.Controllers;

var builder = WebApplication.CreateBuilder(args);

//Config Kafka Producer 
builder.Services.AddSingleton<IProducer<Null, string>>(sp =>
{
    var config = new ProducerConfig
    {
        BootstrapServers = "192.168.178.213:9092" // Troque pelo ip : porta da maquina que está hospendando o apache server
    };
    return new ProducerBuilder<Null, string>(config).Build();
});

builder.Services.AddSingleton<IKafkaProducerService, KafkaProducerService>();




//Config Kafka Consumer
var kafkaConfig = new ConsumerConfig
{
    GroupId = "client-consumer-group",
    BootstrapServers = "192.168.178.213:9092", // Troque pelo ip : porta da maquina que está hospendando o apache server
    AutoOffsetReset = AutoOffsetReset.Earliest
};

builder.Services.AddSingleton<IKafkaConsumerService>(sp =>
{
    var consumer = new ConsumerBuilder<Ignore, string>(kafkaConfig).Build();
    return new KafkaConsumerService(consumer, "response-topic"); // Troque pelo nome do topico que deseja consumir
});

// Add services to the container.
builder.Services.AddRazorPages();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();

app.UseRouting();

app.UseEndpoints(endpoints =>
{
    endpoints.MapControllerRoute(
        name: "default",
        pattern: "{controller=Home}/{action=Index}/{id?}");
});

app.UseAuthorization();

app.MapRazorPages();

app.Run();

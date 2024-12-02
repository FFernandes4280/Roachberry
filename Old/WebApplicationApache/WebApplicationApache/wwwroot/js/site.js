// Please see documentation at https://learn.microsoft.com/aspnet/core/client-side/bundling-and-minification
// for details on configuring this project to bundle and minify static web assets.

// Write your JavaScript code.


async function chamarServico(inputName, nomeServico) {
    $('.preloader').show();

    let inputValue = $('#' + inputName).val()

    await $.ajax({
        type: 'POST',
        url: '/Kafka/SendMessage',
        data: {
            message: inputValue,
            service: nomeServico
        },
        success: function (retorno) {
        },
        error: function (retorno) {
            console.log(retorno);
        },
        complete: function () {
            $('#' + inputName).val("")

            $('.preloader').hide();
        }
    });
    $('#' + inputName).val("")
    $('.preloader').hide();



}

 function buscarServico() {

    $.ajax({
        type: 'GET',
        url: '/Kafka/ConsumeMessages',
        data: {
            cancellationToken: "",
        },
        success: function (retorno) {
            // Assuming 'retorno' contains your message
            $('#messageModal .modal-body').text(retorno.message);
            $('#messageModal').modal('show'); // Show the modal
            buscarServico()
        },
        error: function (retorno) {
            console.log(retorno);
        },
        complete: function () {
            $('.preloader').hide();
        }
    });

}

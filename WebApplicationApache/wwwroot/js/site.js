// Please see documentation at https://learn.microsoft.com/aspnet/core/client-side/bundling-and-minification
// for details on configuring this project to bundle and minify static web assets.

// Write your JavaScript code.


async function chamarServico(inputName, topico, light) {
    $('.preloader').show();

    let input = $('#' + inputName)
    let inputValue;

    if (input) {
        inputValue = $('#' + inputName).val()

    } else {
        inputValue = ''

    }

    await $.ajax({
        type: 'POST',
        url: '/Kafka/SendMessage',
        data: {
            content: inputValue,
            topico: topico,
            light: light
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

function gerarQRCode() {
    const text = document.getElementById("texto").value;

    // Clear any existing QR code
    const qrCodeContainer = document.getElementById("QRCodeImg")
    qrCodeContainer.src = "";

    const apagarbtn = document.getElementById("apagarQRCode")
    apagarbtn.classList.remove("d-none");

    if (text.trim() === "") {
        alert("Please enter some text.");
        return;
    }

    QRCode.toDataURL(text).then(data => {
        qrCodeContainer.src = data;
    })


}
function apagarQRCode() {
    const qrCodeContainer = document.getElementById("QRCodeImg")
    qrCodeContainer.src = "";
    const apagarbtn = document.getElementById("apagarQRCode")
    apagarbtn.classList.add("d-none");

    

}

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace YourNamespace.Pages
{
    public class IndexModel : PageModel
    {
        public string Message { get; set; }
        public bool ShowModal { get; set; }

        public void OnGet(string message = null, bool showModal = false)
        {
            Message = message;
            ShowModal = showModal;
        }
    }
}

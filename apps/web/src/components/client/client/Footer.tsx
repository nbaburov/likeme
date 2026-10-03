import { Typography } from "@material-tailwind/react";
const links = [
	{ name: "Home", href: "/" },
	{ name: "FAQ", href: "/faq" },
	{ name: "Contact", href: "/contact" },
];

const currentYear = new Date().getFullYear();

export function Footer() {
	return (
		<footer className="px-8 py-16 bg-orange-100">
			<div className="container mx-auto flex flex-col items-center">
				<div className="flex flex-wrap items-center justify-center gap-8 pb-8">
					{links.map((link, index) => (
						<ul key={index}>
							<li>
								<Typography
									as="a"
									href={link.href} 
									color="info"
									className="font-medium !text-gray-500 transition-colors hover:!text-gray-900"
								>
									{link.name} 
								</Typography>
							</li>
						</ul>
					))}
				</div>
				<Typography
					color="info"
					className="mt-6 !text-sm !font-normal text-gray-500"
				>
					Copyright &copy; {currentYear} LikeMe
				</Typography>
			</div>
		</footer>
	);
}
export default Footer;

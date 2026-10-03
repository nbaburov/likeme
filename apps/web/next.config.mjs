/** @type {import('next').NextConfig} */
const nextConfig = {
	images: {
		remotePatterns: [
			{
				protocol: "https",
				hostname: "randomuser.me",
				port: "",
				pathname: "/**",
				search: "",
			},
			{
				protocol: "https",
				hostname: "images.unsplash.com",
				port: "",
				pathname: "/**",
				search: "",
			},
			{
				protocol: "http",
				hostname: "localhost",
				port: "8080",
				pathname: "/**",
				search: "",
			},
		],
	},
	output: "standalone",
};

export default nextConfig;

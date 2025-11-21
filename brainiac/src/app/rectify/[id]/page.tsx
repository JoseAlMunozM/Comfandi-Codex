import { RectifyAccountPageTemplate } from "@/presentation";

interface PageProps {
  params: {
    id: string;
  };
}

export default function rectifyPageHome({ params }: PageProps) {  
  return <RectifyAccountPageTemplate id={params.id} />
}
